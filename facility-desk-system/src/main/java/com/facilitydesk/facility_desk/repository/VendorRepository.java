package com.facilitydesk.facility_desk.repository;

import com.facilitydesk.facility_desk.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Page<Vendor> findAllByActiveTrue(Pageable pageable);

    List<Vendor> findByActiveTrueOrderByNameAsc();

    @Query("SELECT v FROM Vendor v WHERE v.active = true AND " +
           "(LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.services) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Vendor> searchVendors(String keyword, Pageable pageable);

    boolean existsByContactEmail(String contactEmail);
}
