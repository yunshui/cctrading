# Phase 3: Order Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement comprehensive order management including order creation, payment integration, order status tracking, and inventory management.

**Architecture:** Spring Boot microservice with MyBatis for data access, Redis for distributed locks, and integration with existing User and Product services.

**Tech Stack:** Spring Boot 2.7.18, MyBatis 3.5.13, MySQL 8.0, Redis 7.x

---

## File Structure

This phase creates the order service:

```
cctrading-platform/
├── pom.xml                          # Update with cc-order-service module
├── cc-common/                       # Add OrderDTO, OrderItemDTO, PaymentDTO
│   └── src/main/java/com/cc/common/dto/
│       ├── OrderDTO.java
│       ├── OrderItemDTO.java
│       ├── CreateOrderRequest.java
│       ├── PaymentDTO.java
│       └── OrderStatus.java
└── cc-order-service/                # NEW
    ├── pom.xml
    └── src/main/
        ├── java/com/cc/order/
        │   ├── OrderServiceApplication.java
        │   ├── model/
        │   │   ├── Order.java
        │   │   ├── OrderItem.java
        │   │   ├── Payment.java
        │   │   └── OrderStatusHistory.java
        │   ├── mapper/
        │   │   ├── OrderMapper.java
        │   │   ├── OrderItemMapper.java
        │   │   ├── PaymentMapper.java
        │   │   └── OrderStatusHistoryMapper.java
        │   ├── service/
        │   │   ├── OrderService.java
        │   │   ├── OrderServiceImpl.java
        │   │   ├── PaymentService.java
        │   │   └── PaymentServiceImpl.java
        │   └── controller/
        │       └── OrderController.java
        └── resources/
            ├── application.yml
            ├── application-docker.yml
            └── mapper/
                ├── OrderMapper.xml
                ├── OrderItemMapper.xml
                ├── PaymentMapper.xml
                └── OrderStatusHistoryMapper.xml
├── docs/
│   └── database/
│       └── ccorder_schema.sql       # NEW
```

---

## Task List

### Task 1: Update Parent POM

**Files:**
- Modify: `pom.xml` (add cc-order-service module)

**Steps:**
- Add cc-order-service to modules list
- Run: `mvn clean compile`
- Commit

### Task 2: Update Common Module - Order DTOs

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/dto/OrderDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/OrderItemDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/CreateOrderRequest.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/PaymentDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/constant/OrderStatus.java`

**Steps:**
1. Create order DTOs with all fields
2. Create order status enum
3. Compile and verify
4. Commit

### Task 3: Create Order Service Database Schema

**Files:**
- Create: `docs/database/ccorder_schema.sql`

**Schema includes:**
- orders table
- order_items table
- payments table
- order_status_history table

**Steps:**
1. Write SQL schema with indexes
2. Include sample data
3. Commit

### Task 4: Create Order Service POM

**Files:**
- Create: `cc-order-service/pom.xml`

**Dependencies:**
- cc-common
- Spring Boot Web
- MyBatis
- MySQL
- Redis
- Nacos Discovery
- OpenFeign (for calling product service)

**Steps:**
1. Write POM with all dependencies
2. Compile and verify
3. Commit

### Task 5: Create Order Models

**Files:**
- Create: `cc-order-service/src/main/java/com/cc/order/model/Order.java`
- Create: `cc-order-service/src/main/java/com/cc/order/model/OrderItem.java`
- Create: `cc-order-service/src/main/java/com/cc/order/model/Payment.java`
- Create: `cc-order-service/src/main/java/com/cc/order/model/OrderStatusHistory.java`

**Steps:**
1. Create all entity models
2. Compile and verify
3. Commit

### Task 6: Create Order Mappers

**Files:**
- Create: `cc-order-service/src/main/java/com/cc/order/mapper/OrderMapper.java`
- Create: `cc-order-service/src/main/resources/mapper/OrderMapper.xml`
- Create: `cc-order-service/src/main/java/com/cc/order/mapper/OrderItemMapper.java`
- Create: `cc-order-service/src/main/resources/mapper/OrderItemMapper.xml`
- Create: `cc-order-service/src/main/java/com/cc/order/mapper/PaymentMapper.java`
- Create: `cc-order-service/src/main/resources/mapper/PaymentMapper.xml`
- Create: `cc-order-service/src/main/java/com/cc/order/mapper/OrderStatusHistoryMapper.java`
- Create: `cc-order-service/src/main/resources/mapper/OrderStatusHistoryMapper.xml`

**Steps:**
1. Write mapper interfaces
2. Write XML files with SQL queries
3. Compile and verify
4. Commit

### Task 7: Create Order Services

**Files:**
- Create: `cc-order-service/src/main/java/com/cc/order/service/OrderService.java`
- Create: `cc-order-service/src/main/java/com/cc/order/service/OrderServiceImpl.java`
- Create: `cc-order-service/src/main/java/com/cc/order/service/PaymentService.java`
- Create: `cc-order-service/src/main/java/com/cc/order/service/PaymentServiceImpl.java`

**Features:**
- Order creation with inventory check
- Order cancellation
- Order status updates
- Payment processing
- Inventory management integration

**Steps:**
1. Write service interfaces
2. Implement with distributed locks
3. Add Feign client for product service
4. Compile and verify
5. Commit

### Task 8: Create Order Controllers

**Files:**
- Create: `cc-order-service/src/main/java/com/cc/order/controller/OrderController.java`

**Endpoints:**
- POST `/api/v1/orders` - Create order
- GET `/api/v1/orders/{id}` - Get order details
- GET `/api/v1/orders` - Get user orders
- PUT `/api/v1/orders/{id}/cancel` - Cancel order
- POST `/api/v1/orders/{id}/pay` - Process payment
- GET `/api/v1/orders/{id}/status` - Get order status

**Steps:**
1. Write controller with proper REST design
2. Add validation and error handling
3. Compile and verify
4. Commit

### Task 9: Create Application Configuration

**Files:**
- Create: `cc-order-service/src/main/java/com/cc/order/OrderServiceApplication.java`
- Create: `cc-order-service/src/main/resources/application.yml`
- Create: `cc-order-service/src/main/resources/application-docker.yml`

**Steps:**
1. Write main application class
2. Configure database, Redis, Nacos
3. Configure Feign clients
4. Compile and verify
5. Commit

### Task 10: Update Gateway Configuration

**Files:**
- Modify: `cc-gateway/src/main/resources/application.yml`
- Modify: `cc-gateway/src/main/resources/application-docker.yml`

**Steps:**
1. Add order service routes
2. Update whitelist if needed
3. Commit

### Task 11: Create Docker Configuration

**Files:**
- Create: `docker/Dockerfile.order`
- Modify: `docker/docker-compose.yml`

**Steps:**
1. Create order service Dockerfile
2. Update docker-compose
3. Commit

### Task 12: Create Phase 3 Documentation

**Files:**
- Create: `docs/phase-3-order-service.md`

**Content:**
- API documentation
- Order flow diagram
- Setup guide
- Testing instructions

**Steps:**
1. Write comprehensive documentation
2. Include example API calls
3. Commit

---

## Completion Checklist

Phase 3 is complete when:
- [ ] All modules compile successfully
- [ ] Database schema is created
- [ ] Order creation works
- [ ] Inventory is properly managed
- [ ] Payment processing works
- [ ] Order status tracking works
- [ ] Gateway routes to order service
- [ ] Docker Compose works
- [ ] Documentation is complete

---

## Next Steps

Proceed to Phase 4: Cart Service implementation