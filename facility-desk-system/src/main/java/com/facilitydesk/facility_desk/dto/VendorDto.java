package com.facilitydesk.facility_desk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class VendorDto {

    @Data
    public static class Request {
        @NotBlank(message = "Vendor name is required")
        private String name;

        private String services;

        @NotBlank(message = "Contact person is required")
        private String contactPerson;

        @NotBlank(message = "Contact email is required")
        @Email(message = "Invalid email format")
        private String contactEmail;

        private String contactPhone;
        private String address;
        private boolean active = true;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String services;
        private String contactPerson;
        private String contactEmail;
        private String contactPhone;
        private String address;
        private boolean active;
        private int totalOrders;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
