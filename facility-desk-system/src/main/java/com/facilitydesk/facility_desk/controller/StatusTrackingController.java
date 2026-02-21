package com.facilitydesk.facility_desk.controller;

import com.facilitydesk.facility_desk.dto.StatusTrackingDto;
import com.facilitydesk.facility_desk.service.StatusTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Status Tracking", description = "Order status tracking and history")
public class StatusTrackingController {

    private final StatusTrackingService statusTrackingService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get full status history for an order")
    public ResponseEntity<List<StatusTrackingDto.Response>> getStatusHistory(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(statusTrackingService.getStatusHistoryForOrder(orderId));
    }

    @GetMapping("/order/{orderId}/latest")
    @Operation(summary = "Get latest status for an order")
    public ResponseEntity<StatusTrackingDto.Response> getLatestStatus(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(statusTrackingService.getLatestStatusForOrder(orderId));
    }
}
