package com.facilitydesk.facility_desk.service;

import com.facilitydesk.facility_desk.dto.OrderDto;
import com.facilitydesk.facility_desk.exception.BadRequestException;
import com.facilitydesk.facility_desk.exception.ResourceNotFoundException;
import com.facilitydesk.facility_desk.model.*;
import com.facilitydesk.facility_desk.repository.OrderRepository;
import com.facilitydesk.facility_desk.repository.StatusTrackingRepository;
import com.facilitydesk.facility_desk.repository.UserRepository;
import com.facilitydesk.facility_desk.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendorService vendorService;
    private final StatusTrackingRepository statusTrackingRepository;

    public Page<OrderDto.Response> getAllOrders(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findAllWithFilters(status, pageable).map(this::toResponse);
    }

    public Page<OrderDto.Response> getOrdersByCurrentUser(Pageable pageable) {
        Long userId = getCurrentUserId();
        return orderRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    public Page<OrderDto.Response> getOrdersByVendor(Long vendorId, Pageable pageable) {
        return orderRepository.findByVendorId(vendorId, pageable).map(this::toResponse);
    }

    public OrderDto.Response getOrderById(Long id) {
        return toResponse(findOrderById(id));
    }

    @Transactional
    public OrderDto.Response createOrder(OrderDto.Request request) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order.OrderBuilder builder = Order.builder()
                .user(user)
                .description(request.getDescription())
                .location(request.getLocation())
                .priority(request.getPriority())
                .status(Order.OrderStatus.PENDING);

        if (request.getVendorId() != null) {
            Vendor vendor = vendorService.findVendorById(request.getVendorId());
            builder.vendor(vendor).status(Order.OrderStatus.ASSIGNED);
        }

        Order order = orderRepository.save(builder.build());

        // Create initial status tracking entry
        addStatusTracking(order, order.getStatus(), "Order created", getCurrentUsername());

        log.info("Order created with id: {}", order.getId());
        return toResponse(order);
    }

    @Transactional
    public OrderDto.Response updateOrderStatus(Long orderId, OrderDto.StatusUpdateRequest request) {
        Order order = findOrderById(orderId);

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        if (request.getVendorId() != null) {
            Vendor vendor = vendorService.findVendorById(request.getVendorId());
            order.setVendor(vendor);
        }

        Order saved = orderRepository.save(order);
        addStatusTracking(saved, request.getStatus(),
                request.getRemarks() != null ? request.getRemarks() : "Status updated from " + oldStatus,
                getCurrentUsername());

        log.info("Order {} status changed from {} to {}", orderId, oldStatus, request.getStatus());
        return toResponse(saved);
    }

    @Transactional
    public OrderDto.Response assignVendor(Long orderId, OrderDto.AssignRequest request) {
        Order order = findOrderById(orderId);

        if (order.getStatus() == Order.OrderStatus.COMPLETED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot assign vendor to a " + order.getStatus() + " order.");
        }

        Vendor vendor = vendorService.findVendorById(request.getVendorId());
        order.setVendor(vendor);
        order.setStatus(Order.OrderStatus.ASSIGNED);

        Order saved = orderRepository.save(order);
        addStatusTracking(saved, Order.OrderStatus.ASSIGNED,
                request.getRemarks() != null ? request.getRemarks() : "Assigned to vendor: " + vendor.getName(),
                getCurrentUsername());

        log.info("Order {} assigned to vendor {}", orderId, vendor.getName());
        return toResponse(saved);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = findOrderById(id);
        order.setStatus(Order.OrderStatus.CANCELLED);
        addStatusTracking(order, Order.OrderStatus.CANCELLED, "Order cancelled", getCurrentUsername());
        orderRepository.save(order);
        log.info("Order {} cancelled", id);
    }

    public List<Order> getUnassignedOrders() {
        return orderRepository.findUnassignedPendingOrders();
    }

    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    private void addStatusTracking(Order order, Order.OrderStatus status, String remarks, String updatedBy) {
        StatusTracking tracking = StatusTracking.builder()
                .order(order)
                .status(status)
                .remarks(remarks)
                .updatedBy(updatedBy)
                .build();
        statusTrackingRepository.save(tracking);
    }

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    private String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    public OrderDto.Response toResponse(Order order) {
        OrderDto.Response r = new OrderDto.Response();
        r.setId(order.getId());
        r.setUserId(order.getUser().getId());
        r.setUsername(order.getUser().getUsername());
        r.setDescription(order.getDescription());
        r.setLocation(order.getLocation());
        r.setPriority(order.getPriority());
        r.setStatus(order.getStatus());
        r.setCreatedAt(order.getCreatedAt());
        r.setUpdatedAt(order.getUpdatedAt());
        if (order.getVendor() != null) {
            r.setVendorId(order.getVendor().getId());
            r.setVendorName(order.getVendor().getName());
        }
        return r;
    }
}
