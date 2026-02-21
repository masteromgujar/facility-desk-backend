package com.facilitydesk.facility_desk.dto;

import com.facilitydesk.facility_desk.model.Order;
import com.facilitydesk.facility_desk.model.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    @Data
    public static class Request {
        @NotNull(message = "Order ID is required")
        private Long orderId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private String paymentMethod;
        private String transactionId;
    }

    @Data
    public static class Response {
        private Long id;
        private Long orderId;
        private BigDecimal amount;
        private Payment.PaymentStatus status;
        private String paymentMethod;
        private String transactionId;
        private LocalDateTime paymentDate;
        private LocalDateTime createdAt;
    }

    @Data
    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private Payment.PaymentStatus status;
        private String transactionId;
    }
}
