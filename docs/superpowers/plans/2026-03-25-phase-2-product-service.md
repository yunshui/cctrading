# Phase 2: Product Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement comprehensive product management including product catalog, categories, inventory, and search capabilities.

**Architecture:** Spring Boot microservice with MyBatis for data access, Redis caching for performance, and integration with Nacos for service discovery.

**Tech Stack:** Spring Boot 2.7.18, MyBatis 3.5.13, MySQL 8.0, Redis 7.x

---

## File Structure

This phase creates the product service:

```
cc-trading-platform/
├── pom.xml                          # Update with cc-product-service module
├── cc-common/                       # Add ProductDTO, CategoryDTO
│   └── src/main/java/com/cc/common/dto/
│       ├── ProductDTO.java
│       ├── CategoryDTO.java
│       ├── ProductSearchRequest.java
│       └── ProductSearchResponse.java
└── cc-product-service/              # NEW
    ├── pom.xml
    └── src/main/
        ├── java/com/cc/product/
        │   ├── ProductServiceApplication.java
        │   ├── model/
        │   │   ├── Product.java
        │   │   ├── Category.java
        │   │   ├── ProductImage.java
        │   │   └── ProductSku.java
        │   ├── mapper/
        │   │   ├── ProductMapper.java
        │   │   ├── CategoryMapper.java
        │   │   ├── ProductImageMapper.java
        │   │   └── ProductSkuMapper.java
        │   ├── service/
        │   │   ├── ProductService.java
        │   │   ├── ProductServiceImpl.java
        │   │   ├── CategoryService.java
        │   │   └── CategoryServiceImpl.java
        │   └── controller/
        │       ├── ProductController.java
        │       └── CategoryController.java
        └── resources/
            ├── application.yml
            ├── application-docker.yml
            └── mapper/
                ├── ProductMapper.xml
                ├── CategoryMapper.xml
                ├── ProductImageMapper.xml
                └── ProductSkuMapper.xml
├── docs/
│   └── database/
│       └── ccproduct_schema.sql    # NEW
```

---

## Task List

### Task 1: Update Parent POM

**Files:**
- Modify: `pom.xml` (add cc-product-service module)

**Steps:**
- Add cc-product-service to modules list
- Run: `mvn clean compile`
- Expected: BUILD SUCCESS
- Commit: `git add pom.xml && git commit -m "feat: add product-service module to parent POM"`

### Task 2: Update Common Module - Product DTOs

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/dto/ProductDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/CategoryDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/ProductSearchRequest.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/ProductSearchResponse.java`
- Update: `cc-common/pom.xml` (add pagination dependencies if needed)

**Steps:**
1. Create ProductDTO with all product fields
2. Create CategoryDTO with category hierarchy support
3. Create search request/response DTOs with pagination
4. Compile: `mvn clean compile -pl cc-common`
5. Commit

### Task 3: Create Product Service Database Schema

**Files:**
- Create: `docs/database/ccproduct_schema.sql`

**Schema includes:**
- products table
- categories table
- product_images table
- product_skus table (for variants like size/color)

**Steps:**
1. Write SQL schema with proper indexes
2. Include sample data
3. Commit

### Task 4: Create Product Service POM

**Files:**
- Create: `cc-product-service/pom.xml`

**Dependencies:**
- cc-common
- Spring Boot Web
- MyBatis
- MySQL
- Redis
- Nacos Discovery
- Spring Cloud OpenFeign (optional, for future)

**Steps:**
1. Write POM with all dependencies
2. Compile: `mvn clean compile -pl cc-product-service`
3. Commit

### Task 5: Create Product Models

**Files:**
- Create: `cc-product-service/src/main/java/com/cc/product/model/Product.java`
- Create: `cc-product-service/src/main/java/com/cc/product/model/Category.java`
- Create: `cc-product-service/src/main/java/com/cc/product/model/ProductImage.java`
- Create: `cc-product-service/src/main/java/com/cc/product/model/ProductSku.java`

**Steps:**
1. Create all entity models with Lombok annotations
2. Compile: `mvn clean compile -pl cc-product-service`
3. Commit

### Task 6: Create Product Mappers

**Files:**
- Create: `cc-product-service/src/main/java/com/cc/product/mapper/ProductMapper.java`
- Create: `cc-product-service/src/main/resources/mapper/ProductMapper.xml`
- Create: `cc-product-service/src/main/java/com/cc/product/mapper/CategoryMapper.java`
- Create: `cc-product-service/src/main/resources/mapper/CategoryMapper.xml`
- Create: `cc-product-service/src/main/java/com/cc/product/mapper/ProductImageMapper.java`
- Create: `cc-product-service/src/main/resources/mapper/ProductImageMapper.xml`
- Create: `cc-product-service/src/main/java/com/cc/product/mapper/ProductSkuMapper.java`
- Create: `cc-product-service/src/main/resources/mapper/ProductSkuMapper.xml`

**Steps:**
1. Write mapper interfaces with CRUD operations
2. Write XML files with SQL queries
3. Include search and pagination queries
4. Compile and verify
5. Commit

### Task 7: Create Product Services

**Files:**
- Create: `cc-product-service/src/main/java/com/cc/product/service/ProductService.java`
- Create: `cc-product-service/src/main/java/com/cc/product/service/ProductServiceImpl.java`
- Create: `cc-product-service/src/main/java/com/cc/product/service/CategoryService.java`
- Create: `cc-product-service/src/main/java/com/cc/product/service/CategoryServiceImpl.java`

**Features:**
- Product CRUD operations
- Category tree management
- Product search with filters
- Redis caching for product data

**Steps:**
1. Write service interfaces
2. Implement with caching logic
3. Compile and verify
4. Commit

### Task 8: Create Product Controllers

**Files:**
- Create: `cc-product-service/src/main/java/com/cc/product/controller/ProductController.java`
- Create: `cc-product-service/src/main/java/com/cc/product/controller/CategoryController.java`

**Endpoints:**
- Products: list, create, update, delete, search, detail
- Categories: list tree, create, update, delete, move

**Steps:**
1. Write controllers with proper REST API design
2. Add validation and error handling
3. Compile and verify
4. Commit

### Task 9: Create Application Configuration

**Files:**
- Create: `cc-product-service/src/main/java/com/cc/product/ProductServiceApplication.java`
- Create: `cc-product-service/src/main/resources/application.yml`
- Create: `cc-product-service/src/main/resources/application-docker.yml`

**Steps:**
1. Write main application class
2. Configure database, Redis, Nacos
3. Configure gateway routes
4. Compile and verify
5. Commit

### Task 10: Update Gateway Configuration

**Files:**
- Modify: `cc-gateway/src/main/resources/application.yml`
- Modify: `cc-gateway/src/main/resources/application-docker.yml`

**Steps:**
1. Add product service routes
2. Add search endpoints to whitelist if needed
3. Commit

### Task 11: Create Docker Configuration

**Files:**
- Create: `docker/Dockerfile.product`
- Modify: `docker/docker-compose.yml`

**Steps:**
1. Create product service Dockerfile
2. Update docker-compose to include product service
3. Commit

### Task 12: Create Phase 2 Documentation

**Files:**
- Create: `docs/phase-2-product-service.md`

**Content:**
- API documentation
- Setup guide
- Testing instructions

**Steps:**
1. Write comprehensive documentation
2. Include example API calls
3. Commit

---

## Completion Checklist

Phase 2 is complete when:
- [ ] All modules compile successfully
- [ ] Database schema is created and tested
- [ ] Product CRUD operations work
- [ ] Category tree management works
- [ ] Product search with filters works
- [ ] Redis caching is configured
- [ ] Gateway routes to product service
- [ ] Docker Compose starts all services
- [ ] API documentation is complete

---

## Next Steps

Proceed to Phase 3: Order Service implementation