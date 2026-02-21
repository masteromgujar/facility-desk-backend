-- ============================================================
-- Facility Desk Management System - MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS facility_desk_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE facility_desk_db;

-- ============================================================
-- Table: roles
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(30)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(120) NOT NULL,
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: user_roles (join table)
-- ============================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: vendors
-- ============================================================
CREATE TABLE IF NOT EXISTS vendors (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    name           VARCHAR(120) NOT NULL,
    services       TEXT,
    contact_person VARCHAR(100) NOT NULL,
    contact_email  VARCHAR(120) NOT NULL,
    contact_phone  VARCHAR(30),
    address        VARCHAR(255),
    active         TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: orders
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     BIGINT        NOT NULL,
    vendor_id   BIGINT,
    description TEXT          NOT NULL,
    location    VARCHAR(255),
    priority    VARCHAR(30),
    status      VARCHAR(30)   NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_order_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_order_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    INDEX idx_order_status   (status),
    INDEX idx_order_user_id  (user_id),
    INDEX idx_order_vendor_id(vendor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: payments
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    order_id       BIGINT         NOT NULL UNIQUE,
    amount         DECIMAL(10, 2) NOT NULL,
    status         VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    payment_date   DATETIME(6),
    created_at     DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id),
    INDEX idx_payment_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Table: status_tracking
-- ============================================================
CREATE TABLE IF NOT EXISTS status_tracking (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    order_id   BIGINT      NOT NULL,
    status     VARCHAR(30) NOT NULL,
    remarks    TEXT,
    updated_by VARCHAR(50),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_st_order FOREIGN KEY (order_id) REFERENCES orders(id),
    INDEX idx_st_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ============================================================
-- SAMPLE DATA
-- Note: Passwords are BCrypt-hashed. Plain-text passwords:
--   admin      -> Admin@123
--   vendor_john-> Vendor@123
--   alice      -> Customer@123
--   emp_carol  -> Employee@123
-- ============================================================

-- Roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_VENDOR'),
('ROLE_CUSTOMER'),
('ROLE_EMPLOYEE');

-- Users (passwords are BCrypt hashed)
INSERT INTO users (username, email, password, active, created_at, updated_at) VALUES
('admin',      'admin@facilitydesk.com',  '$2a$10$7QJ6T8q1AQm6/0NIwWS.R.VH1k7n./kS4kYz8YkWA5.TqKfJ0qkle', 1, NOW(), NOW()),
('vendor_john','john@acmecleaning.com',   '$2a$10$abcdefghijklmnopqrstuuvWwRpqrs.example.hash.vendor1234', 1, NOW(), NOW()),
('vendor_sara','sara@fixitpro.com',       '$2a$10$abcdefghijklmnopqrstuuvWwRpqrs.example.hash.vendor5678', 1, NOW(), NOW()),
('alice',      'alice@company.com',       '$2a$10$abcdefghijklmnopqrstuuvWwRpqrs.example.hash.customer12', 1, NOW(), NOW()),
('bob',        'bob@company.com',         '$2a$10$abcdefghijklmnopqrstuuvWwRpqrs.example.hash.customer34', 1, NOW(), NOW()),
('emp_carol',  'carol@facilitydesk.com',  '$2a$10$abcdefghijklmnopqrstuuvWwRpqrs.example.hash.employee12', 1, NOW(), NOW());

-- NOTE: The hashes above are placeholders for documentation.
--       The application's DataSeeder generates correct BCrypt hashes at startup.
--       If running schema + data manually, generate hashes with:
--         SELECT password('Admin@123');  <-- NOT BCrypt compatible
--       Use a BCrypt tool or the /api/auth/register endpoint instead.

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),  -- admin -> ROLE_ADMIN
(2, 2),  -- vendor_john -> ROLE_VENDOR
(3, 2),  -- vendor_sara -> ROLE_VENDOR
(4, 3),  -- alice -> ROLE_CUSTOMER
(5, 3),  -- bob -> ROLE_CUSTOMER
(6, 4);  -- emp_carol -> ROLE_EMPLOYEE

-- Vendors
INSERT INTO vendors (name, services, contact_person, contact_email, contact_phone, address, active, created_at, updated_at) VALUES
('ACME Cleaning Services',   'Office Cleaning, Floor Polishing, Carpet Cleaning, Window Washing', 'John Smith', 'john@acmecleaning.com', '+1-555-1001', '123 Main Street, New York, NY',       1, NOW(), NOW()),
('FixIt Pro Maintenance',    'Plumbing, Electrical, HVAC, Painting, General Repairs',             'Sara Lee',   'sara@fixitpro.com',      '+1-555-2002', '456 Industrial Ave, Chicago, IL',     1, NOW(), NOW()),
('GreenScape Landscaping',   'Lawn Maintenance, Garden Design, Tree Trimming, Irrigation',        'Tom Green',  'tom@greenscape.com',     '+1-555-3003', '789 Garden Road, Miami, FL',          1, NOW(), NOW()),
('SecureTech Solutions',     'CCTV Installation, Access Control, Security Audits',                'Mike Brown', 'mike@securetech.com',    '+1-555-4004', '321 Tech Park, San Francisco, CA',    1, NOW(), NOW());

-- Orders
INSERT INTO orders (user_id, vendor_id, description, location, priority, status, created_at, updated_at) VALUES
(4, 1, 'Office cleaning needed for floor 3 - meeting rooms and open space',    'Floor 3, Building A',          'HIGH',   'IN_PROGRESS', DATE_SUB(NOW(), INTERVAL 3 DAY),  NOW()),
(4, 2, 'Broken AC unit in conference room B. Urgent fix needed.',              'Conference Room B, Floor 2',   'URGENT', 'ASSIGNED',    DATE_SUB(NOW(), INTERVAL 2 DAY),  NOW()),
(5, NULL,'Request for garden area maintenance before client visit',             'Garden Area, Building Entrance','MEDIUM','PENDING',     DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW()),
(5, 1, 'Deep cleaning of cafeteria after renovation work',                     'Cafeteria, Ground Floor',      'HIGH',   'COMPLETED',   DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(6, 2, 'Leaking pipe under sink in restroom. Water pooling on floor.',         'Restroom, Floor 1',            'URGENT', 'IN_PROGRESS', DATE_SUB(NOW(), INTERVAL 1 DAY),  NOW()),
(5, 3, 'Monthly lawn mowing and hedge trimming for company premises',           'Outdoor Grounds',              'LOW',    'PENDING',     NOW(),                            NOW());

-- Status Tracking
INSERT INTO status_tracking (order_id, status, remarks, updated_by, updated_at) VALUES
(1, 'PENDING',     'Order received',                       'alice',       DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'ASSIGNED',    'Assigned to ACME Cleaning',            'emp_carol',   DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'IN_PROGRESS', 'Cleaning crew dispatched',             'vendor_john', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'PENDING',     'AC breakdown reported',                'alice',       DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'ASSIGNED',    'FixIt Pro assigned',                   'emp_carol',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'PENDING',     'Request created',                      'bob',         DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'PENDING',     'Deep cleaning requested',              'bob',         DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 'ASSIGNED',    'Assigned to ACME Cleaning',            'emp_carol',   DATE_SUB(NOW(), INTERVAL 9 DAY)),
(4, 'IN_PROGRESS', 'Cleaning crew on site',                'vendor_john', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 'COMPLETED',   'Cafeteria cleaning completed',         'vendor_john', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 'PENDING',     'Pipe leak reported',                   'emp_carol',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 'ASSIGNED',    'Plumber assigned',                     'admin',       DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(5, 'IN_PROGRESS', 'Repair work in progress',              'vendor_sara', DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- Payments
INSERT INTO payments (order_id, amount, status, payment_method, transaction_id, payment_date, created_at) VALUES
(1, 350.00, 'PENDING',    'CREDIT_CARD',    'TXN-001-2024', NULL,                            NOW()),
(4, 650.00, 'COMPLETED',  'BANK_TRANSFER',  'TXN-004-2024', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(5, 280.00, 'PROCESSING', 'CREDIT_CARD',    NULL,           NULL,                            NOW());
