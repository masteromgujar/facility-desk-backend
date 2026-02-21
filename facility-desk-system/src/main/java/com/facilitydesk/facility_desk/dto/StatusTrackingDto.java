package com.facilitydesk.facility_desk.dto;

import com.facilitydesk.facility_desk.model.Order;
import lombok.Data;

import java.time.LocalDateTime;

public class StatusTrackingDto {

    @Data
    public static class Response {
        private Long id;
        private Long orderId;
        private Order.OrderStatus status;
        private String remarks;
        private String updatedBy;
        private LocalDateTime updatedAt;
    }
}
