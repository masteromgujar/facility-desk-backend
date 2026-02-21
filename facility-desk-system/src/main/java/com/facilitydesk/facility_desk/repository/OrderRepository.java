package com.facilitydesk.facility_desk.repository;

import com.facilitydesk.facility_desk.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByVendorId(Long vendorId, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

    List<Order> findByVendorIdAndStatus(Long vendorId, Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.vendor IS NULL AND o.status = 'PENDING'")
    List<Order> findUnassignedPendingOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor.id = :vendorId AND o.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") Long vendorId,
                                  @Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.vendor " +
           "WHERE (:status IS NULL OR o.status = :status)")
    Page<Order> findAllWithFilters(@Param("status") Order.OrderStatus status, Pageable pageable);
}
