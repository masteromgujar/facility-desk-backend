package com.facilitydesk.facility_desk.service;

import com.facilitydesk.facility_desk.dto.StatusTrackingDto;
import com.facilitydesk.facility_desk.exception.ResourceNotFoundException;
import com.facilitydesk.facility_desk.model.StatusTracking;
import com.facilitydesk.facility_desk.repository.StatusTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusTrackingService {

    private final StatusTrackingRepository statusTrackingRepository;

    public List<StatusTrackingDto.Response> getStatusHistoryForOrder(Long orderId) {
        return statusTrackingRepository.findByOrderIdOrderByUpdatedAtDesc(orderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StatusTrackingDto.Response getLatestStatusForOrder(Long orderId) {
        StatusTracking latest = statusTrackingRepository.findTopByOrderIdOrderByUpdatedAtDesc(orderId);
        if (latest == null) {
            throw new ResourceNotFoundException("Status tracking not found for order: " + orderId);
        }
        return toResponse(latest);
    }

    public StatusTrackingDto.Response toResponse(StatusTracking st) {
        StatusTrackingDto.Response r = new StatusTrackingDto.Response();
        r.setId(st.getId());
        r.setOrderId(st.getOrder().getId());
        r.setStatus(st.getStatus());
        r.setRemarks(st.getRemarks());
        r.setUpdatedBy(st.getUpdatedBy());
        r.setUpdatedAt(st.getUpdatedAt());
        return r;
    }
}
