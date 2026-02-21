package com.facilitydesk.facility_desk.dto;

import com.facilitydesk.facility_desk.model.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class OrderDto {

    @Data
    public static class Request {
        @NotBlank(message = "Description is required")
        private String description;

        private String location;
        private String priority;
        private Long vendorId;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userId;
        private String username;
        private Long vendorId;
        private String vendorName;
        private String description;
        private String location;
        private String priority;
        private Order.OrderStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class StatusUpdateRequest {
        private Order.OrderStatus status;
        private String remarks;
        private Long vendorId;
    }

    @Data
    public static class AssignRequest {
        private Long vendorId;
        private String remarks;
    }
}
