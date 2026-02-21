package com.facilitydesk.facility_desk.service;

import com.facilitydesk.facility_desk.dto.PaymentDto;
import com.facilitydesk.facility_desk.exception.BadRequestException;
import com.facilitydesk.facility_desk.exception.ResourceNotFoundException;
import com.facilitydesk.facility_desk.model.Order;
import com.facilitydesk.facility_desk.model.Payment;
import com.facilitydesk.facility_desk.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public Page<PaymentDto.Response> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::toResponse);
    }

    public PaymentDto.Response getPaymentById(Long id) {
        return toResponse(findPaymentById(id));
    }

    public PaymentDto.Response getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toResponse(payment);
    }

    @Transactional
    public PaymentDto.Response createPayment(PaymentDto.Request request) {
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            throw new BadRequestException("Payment already exists for order: " + request.getOrderId());
        }

        Order order = orderService.findOrderById(request.getOrderId());
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot create payment for a cancelled order.");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getTransactionId())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created for order {}: {}", request.getOrderId(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public PaymentDto.Response updatePaymentStatus(Long id, PaymentDto.StatusUpdateRequest request) {
        Payment payment = findPaymentById(id);
        payment.setStatus(request.getStatus());

        if (request.getTransactionId() != null) {
            payment.setTransactionId(request.getTransactionId());
        }

        if (request.getStatus() == Payment.PaymentStatus.COMPLETED) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} status updated to {}", id, request.getStatus());
        return toResponse(saved);
    }

    @Transactional
    public void deletePayment(Long id) {
        Payment payment = findPaymentById(id);
        paymentRepository.delete(payment);
        log.info("Payment {} deleted", id);
    }

    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    public PaymentDto.Response toResponse(Payment payment) {
        PaymentDto.Response r = new PaymentDto.Response();
        r.setId(payment.getId());
        r.setOrderId(payment.getOrder().getId());
        r.setAmount(payment.getAmount());
        r.setStatus(payment.getStatus());
        r.setPaymentMethod(payment.getPaymentMethod());
        r.setTransactionId(payment.getTransactionId());
        r.setPaymentDate(payment.getPaymentDate());
        r.setCreatedAt(payment.getCreatedAt());
        return r;
    }
}
