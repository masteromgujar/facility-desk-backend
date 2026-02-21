package com.facilitydesk.facility_desk.config;

import com.facilitydesk.facility_desk.model.*;
import com.facilitydesk.facility_desk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StatusTrackingRepository statusTrackingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Data already seeded, skipping...");
            return;
        }

        log.info("=== Seeding initial data ===");

        // ---- Roles ----
        Role adminRole   = saveRole(Role.RoleName.ROLE_ADMIN);
        Role vendorRole  = saveRole(Role.RoleName.ROLE_VENDOR);
        Role customerRole= saveRole(Role.RoleName.ROLE_CUSTOMER);
        Role employeeRole= saveRole(Role.RoleName.ROLE_EMPLOYEE);

        // ---- Users ----
        User admin = createUser("admin", "admin@facilitydesk.com", "Admin@123", Set.of(adminRole));
        User vendor1User = createUser("vendor_john", "john@acmecleaning.com", "Vendor@123", Set.of(vendorRole));
        User vendor2User = createUser("vendor_sara", "sara@fixitpro.com", "Vendor@123", Set.of(vendorRole));
        User customer1  = createUser("alice", "alice@company.com", "Customer@123", Set.of(customerRole));
        User customer2  = createUser("bob", "bob@company.com", "Customer@123", Set.of(customerRole));
        User employee1  = createUser("emp_carol", "carol@facilitydesk.com", "Employee@123", Set.of(employeeRole));

        // ---- Vendors ----
        Vendor vendor1 = Vendor.builder()
                .name("ACME Cleaning Services")
                .services("Office Cleaning, Floor Polishing, Carpet Cleaning, Window Washing")
                .contactPerson("John Smith")
                .contactEmail("john@acmecleaning.com")
                .contactPhone("9604824677")
                .address("123 Main Street,Pune City, NY")
                .active(true)
                .build();

        Vendor vendor2 = Vendor.builder()
                .name("FixIt Pro Maintenance")
                .services("Plumbing, Electrical, HVAC, Painting, General Repairs")
                .contactPerson("Sara Lee")
                .contactEmail("sara@fixitpro.com")
                .contactPhone("+1-555-2002")
                .address("456 Industrial Ave,Jalgaon Mahanagar , IL")
                .active(true)
                .build();

        Vendor vendor3 = Vendor.builder()
                .name("GreenScape Landscaping")
                .services("Lawn Maintenance, Garden Design, Tree Trimming, Irrigation")
                .contactPerson("Tom Green")
                .contactEmail("tom@greenscape.com")
                .contactPhone("+1-555-3003")
                .address("789 Garden Road, Miami, FL")
                .active(true)
                .build();

        vendorRepository.save(vendor1);
        vendorRepository.save(vendor2);
        vendorRepository.save(vendor3);

        // ---- Orders ----
        Order order1 = Order.builder()
                .user(customer1)
                .vendor(vendor1)
                .description("Office cleaning needed for floor 3 - meeting rooms and open space")
                .location("Floor 3, Building A")
                .priority("HIGH")
                .status(Order.OrderStatus.IN_PROGRESS)
                .build();

        Order order2 = Order.builder()
                .user(customer1)
                .vendor(vendor2)
                .description("Broken air conditioning unit in conference room B. Urgent fix needed.")
                .location("Conference Room B, Floor 2")
                .priority("URGENT")
                .status(Order.OrderStatus.ASSIGNED)
                .build();

        Order order3 = Order.builder()
                .user(customer2)
                .description("Request for garden area maintenance and trimming before client visit")
                .location("Garden Area, Building Entrance")
                .priority("MEDIUM")
                .status(Order.OrderStatus.PENDING)
                .build();

        Order order4 = Order.builder()
                .user(customer2)
                .vendor(vendor1)
                .description("Deep cleaning of cafeteria after renovation work")
                .location("Cafeteria, Ground Floor")
                .priority("HIGH")
                .status(Order.OrderStatus.COMPLETED)
                .build();

        Order order5 = Order.builder()
                .user(employee1)
                .vendor(vendor2)
                .description("Leaking pipe under sink in restroom. Water pooling on floor.")
                .location("Restroom, Floor 1")
                .priority("URGENT")
                .status(Order.OrderStatus.IN_PROGRESS)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);
        orderRepository.save(order5);

        // ---- Status Tracking ----
        seedStatusTracking(order1, Order.OrderStatus.PENDING, "Order received", "alice");
        seedStatusTracking(order1, Order.OrderStatus.ASSIGNED, "Assigned to ACME Cleaning", "emp_carol");
        seedStatusTracking(order1, Order.OrderStatus.IN_PROGRESS, "Cleaning crew dispatched", "vendor_john");

        seedStatusTracking(order2, Order.OrderStatus.PENDING, "AC breakdown reported", "alice");
        seedStatusTracking(order2, Order.OrderStatus.ASSIGNED, "FixIt Pro assigned", "emp_carol");

        seedStatusTracking(order3, Order.OrderStatus.PENDING, "Request created", "bob");

        seedStatusTracking(order4, Order.OrderStatus.PENDING, "Deep cleaning requested", "bob");
        seedStatusTracking(order4, Order.OrderStatus.ASSIGNED, "Assigned to ACME Cleaning", "emp_carol");
        seedStatusTracking(order4, Order.OrderStatus.IN_PROGRESS, "Work started", "vendor_john");
        seedStatusTracking(order4, Order.OrderStatus.COMPLETED, "Cleaning completed successfully", "vendor_john");

        seedStatusTracking(order5, Order.OrderStatus.PENDING, "Pipe leak reported", "emp_carol");
        seedStatusTracking(order5, Order.OrderStatus.ASSIGNED, "Plumber assigned", "admin");
        seedStatusTracking(order5, Order.OrderStatus.IN_PROGRESS, "Repair work in progress", "vendor_sara");

        // ---- Payments ----
        Payment payment1 = Payment.builder()
                .order(order1)
                .amount(new BigDecimal("350.00"))
                .paymentMethod("CREDIT_CARD")
                .transactionId("TXN-001-2024")
                .status(Payment.PaymentStatus.PENDING)
                .build();

        Payment payment4 = Payment.builder()
                .order(order4)
                .amount(new BigDecimal("650.00"))
                .paymentMethod("BANK_TRANSFER")
                .transactionId("TXN-004-2024")
                .status(Payment.PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now().minusDays(5))
                .build();

        Payment payment5 = Payment.builder()
                .order(order5)
                .amount(new BigDecimal("280.00"))
                .paymentMethod("CREDIT_CARD")
                .status(Payment.PaymentStatus.PROCESSING)
                .build();

        paymentRepository.save(payment1);
        paymentRepository.save(payment4);
        paymentRepository.save(payment5);

        log.info("=== Data seeding complete ===");
        log.info("Seeded: 4 Roles, 6 Users, 3 Vendors, 5 Orders, 3 Payments");
        log.info("Login credentials:");
        log.info("  Admin   -> username: admin      | password: Admin@123");
        log.info("  Vendor  -> username: vendor_john | password: Vendor@123");
        log.info("  Customer-> username: alice       | password: Customer@123");
        log.info("  Employee-> username: emp_carol   | password: Employee@123");
    }

    private Role saveRole(Role.RoleName name) {
        return roleRepository.save(new Role(null, name));
    }

    private User createUser(String username, String email, String password, Set<Role> roles) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .active(true)
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    private void seedStatusTracking(Order order, Order.OrderStatus status, String remarks, String updatedBy) {
        StatusTracking tracking = StatusTracking.builder()
                .order(order)
                .status(status)
                .remarks(remarks)
                .updatedBy(updatedBy)
                .build();
        statusTrackingRepository.save(tracking);
    }
}
