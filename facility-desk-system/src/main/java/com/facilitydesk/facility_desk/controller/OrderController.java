package com.facilitydesk.facility_desk.controller;

import com.facilitydesk.facility_desk.dto.OrderDto;
import com.facilitydesk.facility_desk.model.Order;
import com.facilitydesk.facility_desk.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order/Request management endpoints")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR', 'EMPLOYEE')")
    @Operation(summary = "Get all orders (with optional status filter)")
    public ResponseEntity<Page<OrderDto.Response>> getAllOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(status, pageable));
    }

    @GetMapping("/my")
    @Operation(summary = "Get orders for the currently authenticated user")
    public ResponseEntity<Page<OrderDto.Response>> getMyOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByCurrentUser(pageable));
    }

    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    @Operation(summary = "Get orders by vendor ID")
    public ResponseEntity<Page<OrderDto.Response>> getOrdersByVendor(
            @PathVariable Long vendorId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByVendor(vendorId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDto.Response> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new order/request")
    public ResponseEntity<OrderDto.Response> createOrder(
            @Valid @RequestBody OrderDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR', 'EMPLOYEE')")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderDto.Response> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderDto.StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Assign order to a vendor")
    public ResponseEntity<OrderDto.Response> assignVendor(
            @PathVariable Long id,
            @RequestBody OrderDto.AssignRequest request) {
        return ResponseEntity.ok(orderService.assignVendor(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
