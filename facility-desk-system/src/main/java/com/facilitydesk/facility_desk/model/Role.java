package com.facilitydesk.facility_desk.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, unique = true, nullable = false)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN,
        ROLE_VENDOR,
        ROLE_CUSTOMER,
        ROLE_EMPLOYEE
    }
}
