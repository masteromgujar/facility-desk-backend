package com.facilitydesk.facility_desk.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_tracking")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Order.OrderStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column
    private String updatedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime updatedAt;
}
