# Phase 1 Setup Guide

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x
- Nacos 2.2.0

## Quick Start

### 1. Start Infrastructure

Using Docker Compose:
```bash
cd docker
docker-compose up -d mysql redis nacos
```

Or start individually:
```bash
# MySQL
mysql -u root -p < docs/database/ccuser_schema.sql

# Redis
redis-server

# Nacos
cd nacos/bin
./startup.sh -m standalone
```

### 2. Build Services

```bash
mvn clean package
```

### 3. Start Services

```bash
# Gateway
java -jar cc-gateway/target/cc-gateway-1.0.0.jar

# User Service
java -jar cc-user-service/target/cc-user-service-1.0.0.jar
```

### 4. Test API

Login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password"}'
```

Get User Info:
```bash
curl -X GET http://localhost:8080/api/v1/auth/current \
  -H "Authorization: Bearer <token>"
```

## API Documentation

### Authentication

- POST `/api/v1/auth/login` - User login
- GET `/api/v1/auth/current` - Get current user info
- POST `/api/v1/auth/validate` - Validate token

### Address Management

- GET `/api/v1/addresses` - Get user addresses
- POST `/api/v1/addresses` - Create address
- PUT `/api/v1/addresses/{id}` - Update address
- DELETE `/api/v1/addresses/{id}` - Delete address
- POST `/api/v1/addresses/{id}/default` - Set default address