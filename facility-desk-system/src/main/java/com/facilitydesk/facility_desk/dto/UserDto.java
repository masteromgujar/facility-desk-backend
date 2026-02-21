package com.facilitydesk.facility_desk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDto {

    @Data
    public static class Request {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Email
        private String email;

        @Size(min = 6, max = 40)
        private String password;

        private Set<String> roles;
        private boolean active = true;
    }

    @Data
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private boolean active;
        private Set<String> roles;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class UpdateRequest {
        @Email
        private String email;

        @Size(min = 6, max = 40)
        private String password;

        private Set<String> roles;
        private Boolean active;
    }
}
