# Phase 1: Infrastructure, Gateway and User Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Establish the microservices foundation including project structure, API Gateway, and User Service with JWT authentication.

**Architecture:** Spring Cloud Alibaba microservices with Nacos for service discovery and configuration. User Service extends existing user management system with JWT token generation and address management.

**Tech Stack:** Spring Boot 2.7.18, Spring Cloud Alibaba 2021.0.5.0, Nacos 2.2.0, Spring Cloud Gateway 3.1.8, MySQL 8.0, Redis 7.x

---

## File Structure

This phase creates the foundational project structure:

```
cctrading-platform/
├── pom.xml                          # Parent POM with dependency management
├── cc-common/                       # Common shared module
│   ├── pom.xml
│   └── src/main/java/com/cc/common/
│       ├── constant/
│       │   └── ErrorCode.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       ├── dto/
│       │   ├── Result.java
│       │   ├── UserDTO.java
│       │   ├── AddressDTO.java
│       │   └── PageResult.java
│       └── util/
│           ├── JwtUtil.java
│           └── AesUtil.java
├── cc-gateway/
│   ├── pom.xml
│   └── src/main/java/com/cc/gateway/
│       ├── GatewayApplication.java
│       ├── filter/
│       │   └── AuthFilter.java
│       └── config/
│           └── GatewayConfig.java
├── cc-user-service/
│   ├── pom.xml
│   └── src/main/java/com/cc/user/
│       ├── UserServiceApplication.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   └── AddressController.java
│       ├── service/
│       │   ├── AuthService.java
│       │   ├── AuthServiceImpl.java
│       │   ├── AddressService.java
│       │   └── AddressServiceImpl.java
│       ├── mapper/
│       │   ├── UserMapper.java
│       │   ├── UserMapper.xml
│       │   ├── AddressMapper.java
│       │   └── AddressMapper.xml
│       └── model/
│           ├── User.java
│           └── Address.java
├── docs/
│   └── database/
│       └── ccuser_schema.sql
└── docker/
    └── docker-compose.yml
```

---

## Task 1: Create Parent POM

**Files:**
- Create: `pom.xml`

- [ ] **Step 1: Write the parent POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.cc</groupId>
    <artifactId>cctrading-platform</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>cctrading-platform</name>
    <description>Trading Platform Microservices</description>

    <modules>
        <module>cc-common</module>
        <module>cc-gateway</module>
        <module>cc-user-service</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>2.7.18</spring-boot.version>
        <spring-cloud.version>2021.0.8</spring-cloud.version>
        <spring-cloud-alibaba.version>2021.0.5.0</spring-cloud-alibaba.version>
        <mybatis.version>3.5.13</mybatis.version>
        <mybatis-spring.version>2.3.1</mybatis-spring.version>
        <mysql.version>8.0.33</mysql.version>
        <lombok.version>1.18.30</lombok.version>
        <jjwt.version>0.11.5</jjwt.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud Alibaba -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- MyBatis -->
            <dependency>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis</artifactId>
                <version>${mybatis.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis-spring.version}</version>
            </dependency>

            <!-- MySQL -->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>${mysql.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>

            <!-- JWT -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Verify POM syntax**

Run: `mvn validate`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "feat: add parent POM with dependency management"
```

---

## Task 2: Create Common Module - Core Classes

**Files:**
- Create: `cc-common/pom.xml`
- Create: `cc-common/src/main/java/com/cc/common/constant/ErrorCode.java`
- Create: `cc-common/src/main/java/com/cc/common/exception/BusinessException.java`

- [ ] **Step 1: Write common module POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.cc</groupId>
        <artifactId>cctrading-platform</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>cc-common</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Write ErrorCode constant class**

```java
package com.cc.common.constant;

/**
 * 统一错误码定义
 */
public interface ErrorCode {

    // 通用错误码
    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int INTERNAL_ERROR = 500;

    // 用户服务错误码 1001-1999
    int USER_NOT_FOUND = 1001;
    int USER_ALREADY_EXISTS = 1002;
    int INVALID_PASSWORD = 1003;
    int INVALID_TOKEN = 1004;
    int TOKEN_EXPIRED = 1005;
    int ADDRESS_NOT_FOUND = 1006;

    // 商品服务错误码 2001-2999
    int PRODUCT_NOT_FOUND = 2001;
    int INSUFFICIENT_STOCK = 2002;

    // 订单服务错误码 3001-3999
    int ORDER_NOT_FOUND = 3001;
    int ORDER_ALREADY_PAID = 3002;
    int INVALID_ORDER_STATUS = 3003;

    // 支付服务错误码 4001-4999
    int PAYMENT_NOT_FOUND = 4001;
    int PAYMENT_FAILED = 4002;
}
```

- [ ] **Step 3: Write BusinessException class**

```java
package com.cc.common.exception;

import com.cc.common.constant.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_ERROR;
        this.message = message;
    }
}
```

- [ ] **Step 4: Compile common module**

Run: `mvn clean compile -pl cc-common`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add cc-common/
git commit -m "feat: add common module with error handling"
```

---

## Task 3: Create Common Module - Response Classes

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/dto/Result.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/UserDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/AddressDTO.java`
- Create: `cc-common/src/main/java/com/cc/common/dto/PageResult.java`

- [ ] **Step 1: Write Result response class**

```java
package com.cc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 统一响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(com.cc.common.constant.ErrorCode.SUCCESS);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(com.cc.common.constant.ErrorCode.INTERNAL_ERROR, message);
    }
}
```

- [ ] **Step 2: Write UserDTO class**

```java
package com.cc.common.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是4-20位字母、数字、下划线")
    private String username;

    private String phone;

    private Integer role;

    private Integer status;

    private Long createdAt;
}
```

- [ ] **Step 3: Write AddressDTO class**

```java
package com.cc.common.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {

    private Long id;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    private Integer isDefault;

    private Long createdAt;
}
```

- [ ] **Step 4: Write PageResult class**

```java
package com.cc.common.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {

    private Long total;
    private List<T> records;
    private Long current;
    private Long size;

    public PageResult() {}

    public PageResult(Long total, List<T> records, Long current, Long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
    }
}
```

- [ ] **Step 5: Compile and verify**

Run: `mvn clean compile -pl cc-common`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add cc-common/src/main/java/com/cc/common/dto/
git commit -m "feat: add common DTO classes"
```

---

## Task 4: Create Common Module - Global Exception Handler

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Write GlobalExceptionHandler**

```java
package com.cc.common.exception;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("Business exception: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("Validation exception: {}", message);
        return Result.error(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * 绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("Bind exception: {}", message);
        return Result.error(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * 其他系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("System exception", e);
        return Result.error(ErrorCode.INTERNAL_ERROR, "系统内部错误");
    }
}
```

- [ ] **Step 2: Compile and verify**

Run: `mvn clean compile -pl cc-common`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add cc-common/src/main/java/com/cc/common/exception/GlobalExceptionHandler.java
git commit -m "feat: add global exception handler"
```

---

## Task 5: Create Common Module - AES Encryption Utility

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/util/AesUtil.java`

- [ ] **Step 1: Write AesUtil class**

```java
package com.cc.common.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密工具类
 */
@Component
public class AesUtil {

    private static final String AES = "AES";
    private static final String AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";

    /**
     * 生成AES密钥
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 加密
     */
    public static String encrypt(String content, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedContent, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
```

- [ ] **Step 2: Compile and verify**

Run: `mvn clean compile -pl cc-common`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add cc-common/src/main/java/com/cc/common/util/AesUtil.java
git commit -m "feat: add AES encryption utility"
```

---

## Task 6: Create Common Module - JWT Utility

**Files:**
- Create: `cc-common/src/main/java/com/cc/common/util/JwtUtil.java`

- [ ] **Step 1: Write JwtUtil class**

```java
package com.cc.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:cctrading-platform-secret-key-2024}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7天
    private Long refreshExpiration;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "access");

        return createToken(claims, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");

        return createToken(claims, refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中获取角色
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", Integer.class);
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析令牌
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
```

- [ ] **Step 2: Compile and verify**

Run: `mvn clean compile -pl cc-common`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add cc-common/src/main/java/com/cc/common/util/JwtUtil.java
git commit -m "feat: add JWT utility class"
```

---

## Task 5: Create User Service - Database Schema

**Files:**
- Create: `docs/database/ccuser_schema.sql`

- [ ] **Step 1: Write database schema**

```sql
-- =====================================================
-- 用户服务数据库初始化脚本
-- 数据库: ccuser
-- =====================================================

CREATE DATABASE IF NOT EXISTS ccuser
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ccuser;

-- =====================================================
-- 用户表 (users) - 复用现有结构
-- =====================================================
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（AES加密）',
    password_key VARCHAR(255) NOT NULL COMMENT '密码加密密钥',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '账号级别（0=普通用户，1=管理员）',
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态（0=正常，1=锁定）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间',

    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 用户地址表 (user_addresses)
-- =====================================================
DROP TABLE IF EXISTS user_addresses;

CREATE TABLE user_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(11) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_user_id (user_id),
    INDEX idx_is_default (user_id, is_default),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- =====================================================
-- 操作日志表 (operation_logs)
-- =====================================================
DROP TABLE IF EXISTS operation_logs;

CREATE TABLE operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    username VARCHAR(20) NOT NULL COMMENT '操作用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_detail TEXT DEFAULT NULL COMMENT '操作详情',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    result TINYINT NOT NULL COMMENT '操作结果（0=失败，1=成功）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 初始化测试数据
-- =====================================================
-- 注意：密码需要在应用层进行AES加密后插入
```

- [ ] **Step 2: Verify SQL syntax**

Run: `mysql --help 2>&1 | head -1` (check MySQL client availability)
Expected: MySQL version output

- [ ] **Step 3: Commit**

```bash
git add docs/database/ccuser_schema.sql
git commit -m "feat: add user service database schema"
```

---

## Task 6: Create User Service - Core Models

**Files:**
- Create: `cc-user-service/pom.xml`
- Create: `cc-user-service/src/main/java/com/cc/user/model/User.java`
- Create: `cc-user-service/src/main/java/com/cc/user/model/Address.java`

- [ ] **Step 1: Write user service POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.cc</groupId>
        <artifactId>cctrading-platform</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>cc-user-service</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Common Module -->
        <dependency>
            <groupId>com.cc</groupId>
            <artifactId>cc-common</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Write User model**

```java
package com.cc.user.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String passwordKey;
    private Integer role;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
```

- [ ] **Step 3: Write Address model**

```java
package com.cc.user.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户地址实体
 */
@Data
public class Address {

    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 4: Compile user service**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add cc-user-service/
git commit -m "feat: add user service models"
```

---

## Task 7: Create User Service - Mappers

**Files:**
- Create: `cc-user-service/src/main/java/com/cc/user/mapper/UserMapper.java`
- Create: `cc-user-service/src/main/resources/mapper/UserMapper.xml`
- Create: `cc-user-service/src/main/java/com/cc/user/mapper/AddressMapper.java`
- Create: `cc-user-service/src/main/resources/mapper/AddressMapper.xml`

- [ ] **Step 1: Write UserMapper interface**

```java
package com.cc.user.mapper;

import com.cc.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 更新最后登录时间
     */
    int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") String lastLoginAt);
}
```

- [ ] **Step 2: Write UserMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.cc.user.mapper.UserMapper">

    <resultMap id="UserResultMap" type="com.cc.user.model.User">
        <id column="id" property="id"/>
        <result column="username" property="username"/>
        <result column="password" property="password"/>
        <result column="password_key" property="passwordKey"/>
        <result column="role" property="role"/>
        <result column="phone" property="phone"/>
        <result column="status" property="status"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
        <result column="last_login_at" property="lastLoginAt"/>
    </resultMap>

    <select id="findByUsername" resultMap="UserResultMap">
        SELECT * FROM users WHERE username = #{username}
    </select>

    <select id="findById" resultMap="UserResultMap">
        SELECT * FROM users WHERE id = #{id}
    </select>

    <select id="findByPhone" resultMap="UserResultMap">
        SELECT * FROM users WHERE phone = #{phone}
    </select>

    <insert id="insert" parameterType="com.cc.user.model.User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO users (username, password, password_key, role, phone, status)
        VALUES (#{username}, #{password}, #{passwordKey}, #{role}, #{phone}, #{status})
    </insert>

    <update id="update" parameterType="com.cc.user.model.User">
        UPDATE users
        <set>
            <if test="phone != null">phone = #{phone},</if>
            <if test="status != null">status = #{status},</if>
        </set>
        WHERE id = #{id}
    </update>

    <update id="updateLastLogin">
        UPDATE users SET last_login_at = #{lastLoginAt} WHERE id = #{id}
    </update>

</mapper>
```

- [ ] **Step 3: Write AddressMapper interface**

```java
package com.cc.user.mapper;

import com.cc.user.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 地址Mapper
 */
@Mapper
public interface AddressMapper {

    /**
     * 根据ID查询地址
     */
    Address findById(@Param("id") Long id);

    /**
     * 查询用户的所有地址
     */
    List<Address> findByUserId(@Param("userId") Long userId);

    /**
     * 查询用户默认地址
     */
    Address findDefaultByUserId(@Param("userId") Long userId);

    /**
     * 插入地址
     */
    int insert(Address address);

    /**
     * 更新地址
     */
    int update(Address address);

    /**
     * 删除地址
     */
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 设置默认地址
     */
    int setDefault(@Param("userId") Long userId);
}
```

- [ ] **Step 4: Write AddressMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.cc.user.mapper.AddressMapper">

    <resultMap id="AddressResultMap" type="com.cc.user.model.Address">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="receiver_name" property="receiverName"/>
        <result column="receiver_phone" property="receiverPhone"/>
        <result column="province" property="province"/>
        <result column="city" property="city"/>
        <result column="district" property="district"/>
        <result column="detail" property="detail"/>
        <result column="is_default" property="isDefault"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <select id="findById" resultMap="AddressResultMap">
        SELECT * FROM user_addresses WHERE id = #{id}
    </select>

    <select id="findByUserId" resultMap="AddressResultMap">
        SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC
    </select>

    <select id="findDefaultByUserId" resultMap="AddressResultMap">
        SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_default = 1 LIMIT 1
    </select>

    <insert id="insert" parameterType="com.cc.user.model.Address" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user_addresses (user_id, receiver_name, receiver_phone, province, city, district, detail, is_default)
        VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault})
    </insert>

    <update id="update" parameterType="com.cc.user.model.Address">
        UPDATE user_addresses
        <set>
            <if test="receiverName != null">receiver_name = #{receiverName},</if>
            <if test="receiverPhone != null">receiver_phone = #{receiverPhone},</if>
            <if test="province != null">province = #{province},</if>
            <if test="city != null">city = #{city},</if>
            <if test="district != null">district = #{district},</if>
            <if test="detail != null">detail = #{detail},</if>
            <if test="isDefault != null">is_default = #{isDefault},</if>
        </set>
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM user_addresses WHERE id = #{id} AND user_id = #{userId}
    </delete>

    <update id="setDefault">
        UPDATE user_addresses SET is_default = CASE WHEN id = #{id} THEN 1 ELSE 0 END WHERE user_id = #{userId}
    </update>

</mapper>
```

- [ ] **Step 5: Compile and verify**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add cc-user-service/src/main/java/com/cc/user/mapper/ cc-user-service/src/main/resources/mapper/
git commit -m "feat: add user service mappers"
```

---

## Task 8: Create User Service - Auth Service

**Files:**
- Create: `cc-user-service/src/main/java/com/cc/user/service/AuthService.java`
- Create: `cc-user-service/src/main/java/com/cc/user/service/AuthServiceImpl.java`

- [ ] **Step 1: Write AuthService interface**

```java
package com.cc.user.service;

import com.cc.common.dto.Result;
import com.cc.user.model.User;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    Result<?> login(String username, String password);

    /**
     * 根据ID获取用户信息
     */
    Result<User> getUserById(Long id);

    /**
     * 验证Token并返回用户信息
     */
    Result<User> validateToken(String token);
}
```

- [ ] **Step 2: Write AuthServiceImpl**

```java
package com.cc.user.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.common.util.AesUtil;
import com.cc.common.util.JwtUtil;
import com.cc.common.exception.BusinessException;
import com.cc.user.mapper.UserMapper;
import com.cc.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AesUtil aesUtil;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result<?> login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 2. 验证密码（使用AES解密后验证）
        try {
            String decryptedPassword = AesUtil.decrypt(user.getPassword(), user.getPasswordKey());
            if (!password.equals(decryptedPassword)) {
                return Result.error(ErrorCode.INVALID_PASSWORD, "密码错误");
            }
        } catch (Exception e) {
            log.error("Password decryption failed for user: {}", username, e);
            return Result.error(ErrorCode.INTERNAL_ERROR, "密码验证失败");
        }

        // 3. 检查账号状态
        if (user.getStatus() == 1) {
            return Result.error(ErrorCode.BAD_REQUEST, "账号已锁定");
        }

        // 4. 生成JWT Token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 5. 存储到Redis
        String redisKey = TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, 7, TimeUnit.DAYS);

        // 6. 更新最后登录时间
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now().format(FORMATTER));

        // 7. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        return Result.success(data);
    }

    @Override
    public Result<User> getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        // 清除敏感信息
        user.setPassword(null);
        user.setPasswordKey(null);
        return Result.success(user);
    }

    @Override
    public Result<User> validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token无效");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        return getUserById(userId);
    }
}
```

- [ ] **Step 3: Compile and verify**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add cc-user-service/src/main/java/com/cc/user/service/AuthService*
git commit -m "feat: add auth service implementation"
```

---

## Task 9: Create User Service - Address Service

**Files:**
- Create: `cc-user-service/src/main/java/com/cc/user/service/AddressService.java`
- Create: `cc-user-service/src/main/java/com/cc/user/service/AddressServiceImpl.java`

- [ ] **Step 1: Write AddressService interface**

```java
package com.cc.user.service;

import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取用户的所有地址
     */
    Result<List<AddressDTO>> getUserAddresses(Long userId);

    /**
     * 创建地址
     */
    Result<AddressDTO> createAddress(Long userId, AddressDTO addressDTO);

    /**
     * 更新地址
     */
    Result<AddressDTO> updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    /**
     * 删除地址
     */
    Result<Void> deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    Result<Void> setDefaultAddress(Long userId, Long addressId);
}
```

- [ ] **Step 2: Write AddressServiceImpl**

```java
package com.cc.user.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import com.cc.common.exception.BusinessException;
import com.cc.user.mapper.AddressMapper;
import com.cc.user.model.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务实现
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Result<List<AddressDTO>> getUserAddresses(Long userId) {
        List<Address> addresses = addressMapper.findByUserId(userId);
        List<AddressDTO> dtos = addresses.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(dtos);
    }

    @Override
    @Transactional
    public Result<AddressDTO> createAddress(Long userId, AddressDTO addressDTO) {
        Address address = toEntity(addressDTO);
        address.setUserId(userId);

        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.setDefault(userId);
        }

        addressMapper.insert(address);
        return Result.success(toDTO(address));
    }

    @Override
    @Transactional
    public Result<AddressDTO> updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }

        // 更新字段
        if (addressDTO.getReceiverName() != null) {
            address.setReceiverName(addressDTO.getReceiverName());
        }
        if (addressDTO.getReceiverPhone() != null) {
            address.setReceiverPhone(addressDTO.getReceiverPhone());
        }
        if (addressDTO.getProvince() != null) {
            address.setProvince(addressDTO.getProvince());
        }
        if (addressDTO.getCity() != null) {
            address.setCity(addressDTO.getCity());
        }
        if (addressDTO.getDistrict() != null) {
            address.setDistrict(addressDTO.getDistrict());
        }
        if (addressDTO.getDetail() != null) {
            address.setDetail(addressDTO.getDetail());
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.setDefault(userId);
            address.setIsDefault(1);
        }

        addressMapper.update(address);
        return Result.success(toDTO(address));
    }

    @Override
    @Transactional
    public Result<Void> deleteAddress(Long userId, Long addressId) {
        int rows = addressMapper.delete(addressId, userId);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }

        addressMapper.setDefault(userId);
        return Result.success();
    }

    private AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }

    private Address toEntity(AddressDTO dto) {
        Address entity = new Address();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
```

- [ ] **Step 3: Compile and verify**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add cc-user-service/src/main/java/com/cc/user/service/AddressService*
git commit -m "feat: add address service implementation"
```

---

## Task 10: Create User Service - Controllers

**Files:**
- Create: `cc-user-service/src/main/java/com/cc/user/controller/AuthController.java`
- Create: `cc-user-service/src/main/java/com/cc/user/controller/AddressController.java`

- [ ] **Step 1: Write AuthController**

```java
package com.cc.user.controller;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.user.model.User;
import com.cc.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        log.info("Login request for user: {}", username);
        return authService.login(username, password);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(ErrorCode.UNAUTHORIZED, "未认证");
        }
        return authService.getUserById(userId);
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    public Result<User> validateToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return authService.validateToken(token);
    }
}
```

- [ ] **Step 2: Write AddressController**

```java
package com.cc.user.controller;

import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import com.cc.user.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户的所有地址
     */
    @GetMapping
    public Result<List<AddressDTO>> getUserAddresses(@RequestHeader("X-User-Id") Long userId) {
        log.info("Get addresses for user: {}", userId);
        return addressService.getUserAddresses(userId);
    }

    /**
     * 创建地址
     */
    @PostMapping
    public Result<AddressDTO> createAddress(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddressDTO addressDTO) {
        log.info("Create address for user: {}", userId);
        return addressService.createAddress(userId, addressDTO);
    }

    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    public Result<AddressDTO> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressDTO addressDTO) {
        log.info("Update address {} for user: {}", addressId, userId);
        return addressService.updateAddress(userId, addressId, addressDTO);
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        log.info("Delete address {} for user: {}", addressId, userId);
        return addressService.deleteAddress(userId, addressId);
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        log.info("Set default address {} for user: {}", addressId, userId);
        return addressService.setDefaultAddress(userId, addressId);
    }
}
```

- [ ] **Step 3: Compile and verify**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add cc-user-service/src/main/java/com/cc/user/controller/
git commit -m "feat: add user service controllers"
```

---

## Task 11: Create User Service - Application Main Class

**Files:**
- Create: `cc-user-service/src/main/java/com/cc/user/UserServiceApplication.java`
- Create: `cc-user-service/src/main/resources/application.yml`

- [ ] **Step 1: Write application main class**

```java
package com.cc.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cc.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

- [ ] **Step 2: Write application.yml**

```yaml
spring:
  application:
    name: cc-user-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yml
  datasource:
    url: jdbc:mysql://localhost:3306/ccuser?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  redis:
    host: localhost
    port: 6379

server:
  port: 8081

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.cc.user.model

logging:
  level:
    com.cc.user: debug

jwt:
  secret: cctrading-platform-secret-key-2024
  expiration: 86400000
  refresh-expiration: 604800000
```

- [ ] **Step 3: Compile and verify**

Run: `mvn clean compile -pl cc-user-service`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add cc-user-service/src/main/java/com/cc/user/UserServiceApplication.java cc-user-service/src/main/resources/
git commit -m "feat: add user service application configuration"
```

---

## Task 12: Create Gateway Service

**Files:**
- Create: `cc-gateway/pom.xml`
- Create: `cc-gateway/src/main/java/com/cc/gateway/GatewayApplication.java`
- Create: `cc-gateway/src/main/java/com/cc/gateway/filter/AuthFilter.java`

- [ ] **Step 1: Write gateway POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.cc</groupId>
        <artifactId>cctrading-platform</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>cc-gateway</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Common Module -->
        <dependency>
            <groupId>com.cc</groupId>
            <artifactId>cc-common</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <!-- Nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Write gateway application class**

```java
package com.cc.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

- [ ] **Step 3: Write AuthFilter**

```java
package com.cc.gateway.filter;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.common.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<String> WHITELIST = Arrays.asList(
            "/api/v1/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单路径直接放行
        if (isWhitelistPath(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "未提供认证信息");
        }

        String token = authorization.substring(7);

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 将用户信息放入请求头
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Name", username)
                .header("X-User-Role", String.valueOf(role))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isWhitelistPath(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.error(ErrorCode.UNAUTHORIZED, message);
        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to write unauthorized response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
```

- [ ] **Step 4: Write gateway application.yml**

```yaml
spring:
  application:
    name: cc-gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      routes:
        # 用户服务路由
        - id: cc-user-service
          uri: lb://cc-user-service
          predicates:
            - Path=/api/v1/auth/**, /api/v1/addresses/**
          filters:
            - StripPrefix=0

server:
  port: 8080

logging:
  level:
    com.cc.gateway: debug
```

- [ ] **Step 5: Compile and verify**

Run: `mvn clean compile -pl cc-gateway`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add cc-gateway/
git commit -m "feat: add API gateway service"
```

---

## Task 13: Update Parent POM with Gateway Module

**Files:**
- Modify: `pom.xml:14-16`

- [ ] **Step 1: Add gateway module to parent POM**

```xml
    <modules>
        <module>cc-common</module>
        <module>cc-gateway</module>
        <module>cc-user-service</module>
    </modules>
```

- [ ] **Step 2: Compile entire project**

Run: `mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "chore: add gateway module to parent POM"
```

---

## Task 14: Create Gateway Configuration

**Files:**
- Create: `cc-gateway/src/main/java/com/cc/gateway/config/GatewayConfig.java`

- [ ] **Step 1: Write GatewayConfig**

```java
package com.cc.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 网关配置
 */
@Configuration
public class GatewayConfig {

    /**
     * 跨域配置
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
```

- [ ] **Step 2: Compile and verify**

Run: `mvn clean compile -pl cc-gateway`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add cc-gateway/src/main/java/com/cc/gateway/config/GatewayConfig.java
git commit -m "feat: add gateway configuration"
```

---

## Task 15: Create Docker Profile Configurations

**Files:**
- Create: `cc-gateway/src/main/resources/application-docker.yml`
- Create: `cc-user-service/src/main/resources/application-docker.yml`

- [ ] **Step 1: Write Gateway Docker profile**

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: nacos:8848

jwt:
  secret: ${JWT_SECRET:cctrading-platform-secret-key-2024-docker}
  expiration: 86400000
  refresh-expiration: 604800000

logging:
  level:
    com.cc.gateway: info
```

- [ ] **Step 2: Write User Service Docker profile**

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/ccuser?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  redis:
    host: redis
    port: 6379
  cloud:
    nacos:
      discovery:
        server-addr: nacos:8848

jwt:
  secret: ${JWT_SECRET:cctrading-platform-secret-key-2024-docker}

logging:
  level:
    com.cc.user: info
```

- [ ] **Step 3: Compile and verify**

Run: `mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add cc-gateway/src/main/resources/application-docker.yml cc-user-service/src/main/resources/application-docker.yml
git commit -m "feat: add Docker profile configurations"
```

---

## Task 16: Create Docker Files

**Files:**
- Create: `docker/Dockerfile.gateway`
- Create: `docker/Dockerfile.user`

- [ ] **Step 1: Write Gateway Dockerfile**

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY cc-gateway/target/cc-gateway-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 2: Write User Service Dockerfile**

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY cc-user-service/target/cc-user-service-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 3: Commit**

```bash
git add docker/Dockerfile.gateway docker/Dockerfile.user
git commit -m "feat: add Dockerfiles for services"
```

---

## Task 16: Create Docker Compose for Development

**Files:**
- Create: `docker/docker-compose.yml`

- [ ] **Step 1: Write docker-compose.yml**

```yaml
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v2.2.0
    ports:
      - "8848:8848"
    environment:
      - MODE=standalone
    volumes:
      - ./nacos/logs:/home/nacos/logs

  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=ccuser
    volumes:
      - ./mysql/data:/var/lib/mysql
      - ./mysql/init:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - ./redis/data:/data
    command: redis-server --appendonly yes

  gateway:
    build:
      context: ..
      dockerfile: docker/Dockerfile.gateway
    ports:
      - "8080:8080"
    depends_on:
      - nacos
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  user-service:
    build:
      context: ..
      dockerfile: docker/Dockerfile.user
    ports:
      - "8081:8081"
    depends_on:
      - nacos
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=docker

volumes:
  mysql_data:
  redis_data:
  nacos_logs:
```

- [ ] **Step 2: Commit**

```bash
git add docker/docker-compose.yml
git commit -m "feat: add Docker Compose for development"
```

---

## Task 17: Create Phase 1 Documentation

**Files:**
- Create: `docs/phase-1-setup-guide.md`

- [ ] **Step 1: Write setup guide**

```markdown
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
```

- [ ] **Step 2: Commit**

```bash
git add docs/phase-1-setup-guide.md
git commit -m "docs: add phase 1 setup guide"
```

---

## Completion Checklist

Phase 1 is complete when:
- [ ] All modules compile successfully
- [ ] Database schema is created
- [ ] Gateway can route requests to User Service
- [ ] JWT authentication is working
- [ ] Address management APIs are functional
- [ ] Docker Compose can start all infrastructure components
- [ ] Setup guide is documented

---

## Next Steps

Proceed to Phase 2: Product Service implementation