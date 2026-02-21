# 🏢 Facility Desk Management System — Backend

A fully functional **Spring Boot 3** + **JWT Security** + **MySQL** backend for managing facility requests, vendors, orders, payments, and status tracking.

---

## 📁 Project Structure

```
src/main/java/com/facilitydesk/facility_desk/
├── config/
│   ├── DataSeeder.java          ← Seeds roles, users, vendors, orders on startup
│   ├── SecurityConfig.java      ← JWT security filter chain + role-based access
│   └── SwaggerConfig.java       ← OpenAPI 3 / Swagger UI configuration
├── controller/
│   ├── AuthController.java      ← /api/auth/login, /api/auth/register
│   ├── UserController.java      ← /api/users  (Admin only)
│   ├── VendorController.java    ← /api/vendors
│   ├── OrderController.java     ← /api/orders
│   ├── PaymentController.java   ← /api/payments
│   └── StatusTrackingController ← /api/status
├── dto/
│   ├── AuthDto.java
│   ├── UserDto.java
│   ├── VendorDto.java
│   ├── OrderDto.java
│   ├── PaymentDto.java
│   └── StatusTrackingDto.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java  ← @RestControllerAdvice
├── model/
│   ├── Role.java
│   ├── User.java
│   ├── Vendor.java
│   ├── Order.java
│   ├── Payment.java
│   └── StatusTracking.java
├── repository/
│   ├── RoleRepository.java
│   ├── UserRepository.java
│   ├── VendorRepository.java
│   ├── OrderRepository.java
│   ├── PaymentRepository.java
│   └── StatusTrackingRepository.java
├── security/
│   ├── UserDetailsImpl.java
│   ├── UserDetailsServiceImpl.java
│   ├── JwtUtils.java
│   ├── AuthTokenFilter.java
│   └── AuthEntryPointJwt.java
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── VendorService.java
    ├── OrderService.java
    ├── PaymentService.java
    └── StatusTrackingService.java
```

---

## ⚙️ Prerequisites

| Tool         | Version  |
|--------------|----------|
| Java         | 17+      |
| Maven        | 3.8+     |
| MySQL        | 8.0+     |
| Postman      | Any      |

---

## 🚀 Running the Application

### Step 1 — Set up MySQL

```sql
CREATE DATABASE facility_desk_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Step 2 — Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/facility_desk_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root        # ← change to your MySQL user
spring.datasource.password=root        # ← change to your MySQL password
```

### Step 3 — Build & Run

```bash
cd facility-desk-system
mvn clean install -DskipTests
mvn spring-boot:run
```

The app will start on **http://localhost:8080**.  
On first run, `DataSeeder` automatically creates all tables and inserts sample data.

---

## 🔐 Default Credentials (seeded at startup)

| Role     | Username     | Password      |
|----------|--------------|---------------|
| Admin    | `admin`      | `Admin@123`   |
| Vendor   | `vendor_john`| `Vendor@123`  |
| Vendor   | `vendor_sara`| `Vendor@123`  |
| Customer | `alice`      | `Customer@123`|
| Customer | `bob`        | `Customer@123`|
| Employee | `emp_carol`  | `Employee@123`|

---

## 📖 Swagger UI

Access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

Or JSON spec at:

```
http://localhost:8080/api-docs
```

---

## 🔑 Role-Based Access Control

| Endpoint              | ADMIN | VENDOR | CUSTOMER | EMPLOYEE |
|-----------------------|-------|--------|----------|----------|
| POST /api/auth/**     | ✅    | ✅     | ✅       | ✅       |
| GET  /api/users/**    | ✅    | ❌     | ❌       | ❌       |
| PUT  /api/users/**    | ✅    | ❌     | ❌       | ❌       |
| GET  /api/vendors/**  | ✅    | ✅     | ✅       | ✅       |
| POST /api/vendors/**  | ✅    | ❌     | ❌       | ❌       |
| GET  /api/orders/**   | ✅    | ✅     | ✅*      | ✅       |
| POST /api/orders/**   | ✅    | ❌     | ✅       | ✅       |
| PUT  /api/orders/**/status | ✅ | ✅  | ❌       | ✅       |
| PUT  /api/orders/**/assign | ✅ | ❌  | ❌       | ✅       |
| DELETE /api/orders/** | ✅    | ❌     | ❌       | ❌       |
| /api/payments/**      | ✅    | ❌     | ✅       | ✅       |
| /api/status/**        | ✅    | ✅     | ✅       | ✅       |

*Customers see only their own orders via `/api/orders/my`

---

## 🧪 Testing with Postman

### 1. Register & Login

**POST** `http://localhost:8080/api/auth/login`
```json
{
  "username": "admin",
  "password": "Admin@123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@facilitydesk.com",
  "roles": ["ROLE_ADMIN"]
}
```

> Copy the `token` value and use it as `Authorization: Bearer <token>` in all subsequent requests.

---

### 2. Set Bearer Token in Postman

In each request:
- Go to **Authorization** tab
- Select **Bearer Token**
- Paste the JWT token

---

### 3. Create a New Order (as Customer)

First login as `alice` / `Customer@123` to get a customer token.

**POST** `http://localhost:8080/api/orders`
```json
{
  "description": "Elevator maintenance required on floor 5",
  "location": "Floor 5, Building B",
  "priority": "HIGH",
  "vendorId": null
}
```

---

### 4. Get All Orders (Admin/Employee)

**GET** `http://localhost:8080/api/orders?page=0&size=10&sort=createdAt,desc`

Filter by status:

**GET** `http://localhost:8080/api/orders?status=PENDING&page=0&size=5`

---

### 5. Assign Order to Vendor (Admin/Employee)

**PUT** `http://localhost:8080/api/orders/3/assign`
```json
{
  "vendorId": 3,
  "remarks": "GreenScape assigned for garden maintenance"
}
```

---

### 6. Update Order Status (Admin/Vendor/Employee)

**PUT** `http://localhost:8080/api/orders/3/status`
```json
{
  "status": "IN_PROGRESS",
  "remarks": "Work has begun"
}
```

---

### 7. Create Payment for an Order

**POST** `http://localhost:8080/api/payments`
```json
{
  "orderId": 3,
  "amount": 199.99,
  "paymentMethod": "CREDIT_CARD",
  "transactionId": "TXN-DEMO-001"
}
```

---

### 8. Update Payment Status

**PUT** `http://localhost:8080/api/payments/1/status`
```json
{
  "status": "COMPLETED",
  "transactionId": "TXN-CONFIRMED-001"
}
```

---

### 9. Get Status History for an Order

**GET** `http://localhost:8080/api/status/order/1`

---

### 10. Get All Vendors (public, no auth required)

**GET** `http://localhost:8080/api/vendors?page=0&size=10&sort=name,asc`

Search vendors:

**GET** `http://localhost:8080/api/vendors/search?keyword=cleaning`

---

### 11. Register New User

**POST** `http://localhost:8080/api/auth/register`
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "Pass@1234",
  "roles": ["customer"]
}
```
Valid role values: `admin`, `vendor`, `customer`, `employee`

---

### 12. Manage Users (Admin Only)

**GET** `http://localhost:8080/api/users`

**GET** `http://localhost:8080/api/users/1`

**PUT** `http://localhost:8080/api/users/4`
```json
{
  "email": "alice.updated@company.com",
  "roles": ["customer", "employee"]
}
```

**DELETE** `http://localhost:8080/api/users/4`  ← soft-deletes (deactivates)

---

## 📊 API Endpoints Summary

| Method | Endpoint                          | Description                   | Roles Allowed           |
|--------|-----------------------------------|-------------------------------|-------------------------|
| POST   | /api/auth/login                   | Login (get JWT)               | Public                  |
| POST   | /api/auth/register                | Register new user             | Public                  |
| GET    | /api/users                        | List all users (paginated)    | ADMIN                   |
| GET    | /api/users/{id}                   | Get user by ID                | ADMIN                   |
| PUT    | /api/users/{id}                   | Update user                   | ADMIN                   |
| DELETE | /api/users/{id}                   | Deactivate user               | ADMIN                   |
| GET    | /api/vendors                      | List vendors (paginated)      | All                     |
| GET    | /api/vendors/search?keyword=      | Search vendors                | All                     |
| GET    | /api/vendors/{id}                 | Get vendor by ID              | All                     |
| POST   | /api/vendors                      | Create vendor                 | ADMIN                   |
| PUT    | /api/vendors/{id}                 | Update vendor                 | ADMIN, VENDOR           |
| DELETE | /api/vendors/{id}                 | Deactivate vendor             | ADMIN                   |
| GET    | /api/orders                       | List all orders               | ADMIN, VENDOR, EMPLOYEE |
| GET    | /api/orders/my                    | My orders (current user)      | All                     |
| GET    | /api/orders/vendor/{vendorId}     | Orders by vendor              | ADMIN, VENDOR           |
| GET    | /api/orders/{id}                  | Get order by ID               | All (authenticated)     |
| POST   | /api/orders                       | Create order                  | ADMIN, CUSTOMER, EMPLOYEE|
| PUT    | /api/orders/{id}/status           | Update order status           | ADMIN, VENDOR, EMPLOYEE |
| PUT    | /api/orders/{id}/assign           | Assign vendor to order        | ADMIN, EMPLOYEE         |
| DELETE | /api/orders/{id}                  | Cancel order                  | ADMIN                   |
| GET    | /api/payments                     | List all payments             | ADMIN                   |
| GET    | /api/payments/{id}                | Get payment by ID             | ADMIN, CUSTOMER, EMPLOYEE|
| GET    | /api/payments/order/{orderId}     | Get payment by order          | ADMIN, CUSTOMER, EMPLOYEE|
| POST   | /api/payments                     | Create payment                | ADMIN, CUSTOMER, EMPLOYEE|
| PUT    | /api/payments/{id}/status         | Update payment status         | ADMIN, EMPLOYEE         |
| DELETE | /api/payments/{id}                | Delete payment                | ADMIN                   |
| GET    | /api/status/order/{orderId}       | Status history for order      | All (authenticated)     |
| GET    | /api/status/order/{orderId}/latest| Latest status for order       | All (authenticated)     |

---

## 🛠️ Configuration Properties

```properties
# JWT expiration (default: 24 hours = 86400000 ms)
app.jwt.expiration-ms=86400000

# JWT secret — change this in production!
app.jwt.secret=FacilityDeskSecretKey2024@SuperSecureKeyLongEnoughFor256BitHMAC

# Database
spring.jpa.hibernate.ddl-auto=create-drop   # Use 'update' in production
```

---

## 🔧 Production Checklist

- [ ] Change `spring.jpa.hibernate.ddl-auto` from `create-drop` to `update` or `validate`
- [ ] Use environment variables for DB credentials and JWT secret
- [ ] Set a long, random JWT secret (256-bit minimum)
- [ ] Enable HTTPS / TLS
- [ ] Configure CORS properly for your frontend domain
- [ ] Use connection pooling (HikariCP is auto-configured)
- [ ] Set up log rotation and monitoring

---

## 📦 Key Dependencies

| Library            | Version  | Purpose                         |
|--------------------|----------|---------------------------------|
| Spring Boot        | 3.2.5    | Core framework                  |
| Spring Security    | (Boot)   | Authentication & authorization  |
| Spring Data JPA    | (Boot)   | ORM / database access           |
| MySQL Connector    | (Boot)   | MySQL JDBC driver               |
| JJWT              | 0.11.5   | JWT token generation/validation |
| SpringDoc OpenAPI  | 2.5.0    | Swagger / API docs              |
| Lombok             | (Boot)   | Boilerplate reduction           |
| Bean Validation    | (Boot)   | Request validation (@Valid)     |
