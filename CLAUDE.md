# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CC Trading Platform is a microservices-based B2C + C2C trading platform built with Spring Cloud Alibaba. The project supports both physical and virtual goods, real payment integration (Alipay/WeChat), and uses a modular architecture.

**Tech Stack:**
- Java 17
- Spring Boot 2.7.18, Spring Cloud 2021.0.8, Spring Cloud Alibaba 2021.0.5.0
- Nacos 2.2.0 (service discovery + config center)
- Spring Cloud Gateway 3.1.8
- MyBatis 3.5.13
- MySQL 8.0, Redis 7.x
- JWT (jjwt 0.11.5)

## Build Commands

```bash
# Build all modules and install to local Maven repository
mvn clean install -DskipTests

# Build specific module
mvn clean compile -pl cc-common
mvn clean compile -pl cc-user-service
mvn clean compile -pl cc-product-service
mvn clean compile -pl cc-order-service

# Package for deployment
mvn clean package
```

## Architecture

The platform consists of 5 microservices:

1. **cc-gateway** (Port 8080): API Gateway with JWT authentication filter
2. **cc-user-service** (Port 8081): User authentication and address management
3. **cc-product-service** (Port 8082): Product catalog and categories
4. **cc-order-service** (Port 8083): Order management (in progress)
5. **cc-common**: Shared utilities, DTOs, and error handling

### Service Communication

- **Gateway to Services**: Routes requests based on URL patterns, adds JWT-validated headers (`X-User-Id`, `X-User-Name`, `X-User-Role`)
- **Service to Service**: OpenFeign (for order service to call product/user services)
- **API Version**: All endpoints use `/api/v1` prefix

### Authentication Flow

1. User login via `POST /api/v1/auth/login` → returns JWT access + refresh tokens
2. Client includes token in `Authorization: Bearer <token>` header
3. Gateway validates token and forwards to downstream services with user context
4. Services use injected headers for authorization

## Key Modules

### cc-common

Shared module containing:
- `Result<T>`: Unified response wrapper with code, message, and data
- `PageResult<T>`: Pagination result with total, records, current page, page size
- `ErrorCode`: Error code constants (SUCCESS=200, UNAUTHORIZED=401, NOT_FOUND=404, etc.)
- `BusinessException`: Custom runtime exception
- `GlobalExceptionHandler`: Centralized exception handling
- `JwtUtil`: Token generation and validation (uses secret from config)
- `AesUtil`: AES encryption for passwords

### Gateway Configuration

Routes are defined in `cc-gateway/src/main/resources/application.yml`:

```yaml
spring.cloud.gateway.routes:
  - id: cc-user-service
    uri: lb://cc-user-service
    predicates:
      - Path=/api/v1/auth/**, /api/v1/addresses/**
  - id: cc-product-service
    uri: lb://cc-product-service
    predicates:
      - Path=/api/v1/products/**, /api/v1/categories/**
```

Auth filter whitelist includes: `/api/v1/auth/login`, `/api/v1/products`, `/api/v1/categories`

## Database Schema

Each service has its own database:

- **ccuser**: users table, user_addresses table
- **ccproduct**: products, categories, product_images, product_skus tables
- **ccorder**: orders, order_items, payments, order_status_history tables

Schema files are in `docs/database/`.

## Running the Application

### With Docker Compose

```bash
cd docker
docker-compose up -d mysql redis nacos
docker-compose up gateway user-service product-service
```

### Manual Start

```bash
# Start infrastructure first
# Then build and start services
mvn clean package
java -jar cc-gateway/target/cc-gateway-1.0.0.jar
java -jar cc-user-service/target/cc-user-service-1.0.0.jar
java -jar cc-product-service/target/cc-product-service-1.0.0.jar
```

## Development Notes

### MyBatis Configuration

Each service with MyBatis uses:
- `@MapperScan("com.cc.<service>.mapper")` in main application class
- Mapper XML files in `src/main/resources/mapper/`
- `type-aliases-package: com.cc.<service>.model` in application.yml

### Service Layer Pattern

Standard Spring Boot pattern with interface + implementation:
- Service interfaces in `src/main/java/com/cc/<service>/service/`
- Implementations in `src/main/java/com/cc/<service>/service/*Impl.java`

### Redis Caching

Services use `StringRedisTemplate` for caching with expiration:
- Product details: cached for 1 hour
- Category tree: cached for 2 hours
- Cache keys follow pattern: `<entity>:<id>`

### Error Code Ranges

- 1001-1999: User service errors
- 2001-2999: Product service errors
- 3001-3999: Order service errors
- 4001-4999: Payment service errors

## Testing APIs

### User Service

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password"}'

# Get current user (requires token)
curl -X GET http://localhost:8080/api/v1/auth/current \
  -H "Authorization: Bearer <token>"
```

### Product Service

```bash
# Get category tree
curl http://localhost:8080/api/v1/categories/tree

# Get featured products
curl http://localhost:8080/api/v1/products/featured?pageNum=1&pageSize=10

# Search products
curl -X POST http://localhost:8080/api/v1/products/search \
  -H "Content-Type: application/json" \
  -d '{"keyword":"iPhone","pageNum":1,"pageSize":10}'
```

## Phase Implementation Status

- **Phase 1**: ✅ Completed - Infrastructure, Gateway, User Service
- **Phase 2**: ✅ Completed - Product Service
- **Phase 3**: 🚧 In Progress - Order Service
- **Phase 4**: ⏳ Pending - Payment Service

See `PROGRESS.md` and `PROGRESS_PHASE*.md` for detailed task tracking.

## Project Configuration

Parent POM manages all dependencies and versions. Key versions:
- `spring-boot.version`: 2.7.18
- `spring-cloud.version`: 2021.0.8
- `spring-cloud-alibaba.version`: 2021.0.5.0
- `mybatis.version`: 3.5.13
- `mysql.version`: 8.0.33
- `jjwt.version`: 0.11.5

When adding new dependencies, add them to the parent POM's `<dependencyManagement>` section first.