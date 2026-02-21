package com.facilitydesk.facility_desk.repository;

import com.facilitydesk.facility_desk.model.StatusTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusTrackingRepository extends JpaRepository<StatusTracking, Long> {

    List<StatusTracking> findByOrderIdOrderByUpdatedAtDesc(Long orderId);

    StatusTracking findTopByOrderIdOrderByUpdatedAtDesc(Long orderId);
}
