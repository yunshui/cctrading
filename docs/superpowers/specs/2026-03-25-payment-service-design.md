# Phase 4: Independent Payment Service Design

**Date:** 2026-03-25
**Status:** Design Approved

## Overview

Create `cc-payment-service` - a dedicated microservice for handling Alipay and WeChat Pay integrations, supporting both payment and refund operations with synchronous (redirect) and asynchronous (callback) flows.

## Motivation

Phase 3 implemented basic payment handling within the order service, but it lacks real payment gateway integration. Creating a separate payment service allows:

1. **Separation of concerns** - Order service focuses on order logic, payment service handles gateway specifics
2. **Easier testing** - Can mock payment service without touching order logic
3. **Independent scaling** - Payment traffic can scale separately from order traffic
4. **Gateway abstraction** - Easy to add new payment methods in the future

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                          │
│  /api/v1/payments/** → cc-payment-service (8084)               │
└─────────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   ┌─────────┐        ┌─────────┐        ┌─────────┐
   │  Order  │        │Payment  │        │ Alipay  │
   │ Service │◄──────►│ Service │◄──────► │ Gateway │
   └─────────┘        │ (8084)  │        └─────────┘
                       └─────────┘               │
                          │                      │
                          ▼                      ▼
                   ┌─────────┐           ┌─────────┐
                   │  MySQL  │           │WeChat   │
                   │ ccpay   │           │Pay API  │
                   └─────────┘           └─────────┘
                          │
                          ▼
                      ┌───────┐
                      │ Redis │
                      └───────┘
```

## Module Structure

```
cc-payment-service/
├── pom.xml
└── src/main/
    ├── java/com/cc/payment/
    │   ├── PaymentServiceApplication.java
    │   ├── model/
    │   │   ├── Payment.java
    │   │   ├── PaymentCallback.java
    │   │   ├── Refund.java
    │   │   └── RefundCallback.java
    │   ├── mapper/
    │   │   ├── PaymentMapper.java
    │   │   ├── RefundMapper.java
    │   │   └── CallbackMapper.java
    │   ├── service/
    │   │   ├── PaymentService.java
    │   │   ├── PaymentServiceImpl.java
    │   │   ├── RefundService.java
    │   │   ├── RefundServiceImpl.java
    │   │   ├── AlipayService.java
    │   │   └── WechatPayService.java
    │   └── controller/
    │       ├── PaymentController.java
    │       └── RefundController.java
    └── resources/
        ├── application.yml
        ├── application-docker.yml
        ├── mapper/
        │   ├── PaymentMapper.xml
        │   └── RefundMapper.xml
        └── alipay/
            ├── alipay_public_key.pem
            └── app_private_key.pem
```

## Components

### Payment Models

- **Payment** - Stores payment records (moved from order service to payment service)
- **PaymentCallback** - Logs all gateway callbacks for audit/replay
- **Refund** - Stores refund requests and status
- **RefundCallback** - Logs refund callbacks

### Gateway Services (Strategy Pattern)

- **PaymentGateway interface** - Common contract for all gateways
- **AlipayService** - Alipay SDK integration (using alipay-sdk-java)
- **WechatPayService** - WeChat Pay API v3 integration

### Controllers

- **PaymentController** - Create payment, query status, handle callbacks
- **RefundController** - Create refund, query refund status

## Data Flows

### Payment Flow (Alipay Website Pay)

```
1. Order Service → Payment Service: createPayment(orderId, amount, method)
2. Payment Service → Alipay API: Generate payment URL
3. Payment Service → Order Service: Return payment URL
4. User → Alipay: Redirect to payment page
5. User → Alipay: Complete payment
6. Alipay → Payment Service: Async callback (notify_url)
7. Payment Service: Verify signature, update status, notify Order Service
8. User → Payment Service: Sync redirect (return_url) → Order Service
```

### Refund Flow

```
1. Order Service → Payment Service: createRefund(paymentId, amount, reason)
2. Payment Service → Gateway API: Submit refund request
3. Payment Service → Order Service: Return refund result
4. Gateway → Payment Service: Async callback (refund_notify_url)
5. Payment Service: Update refund status, notify Order Service
```

### Order Service Integration

Order Service will use OpenFeign to call Payment Service:

```java
@FeignClient(name = "cc-payment-service")
public interface PaymentClient {
    @PostMapping("/api/v1/payments/create")
    Result<PaymentDTO> createPayment(@RequestBody CreatePaymentRequest request);

    @GetMapping("/api/v1/payments/{id}")
    Result<PaymentDTO> getPayment(@PathVariable Long id);
}
```

## Error Handling

- **Gateway signature verification**: Reject all callbacks with invalid signatures
- **Idempotency**: Use Redis locks to prevent duplicate callback processing
- **Timeout handling**: Retry failed gateway calls with exponential backoff
- **Payment query fallback**: If async callback fails, periodically query payment status from gateway

## Database Schema

```sql
-- Database: ccpay

-- Payment table (enhanced from order service)
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    payment_no VARCHAR(64) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method TINYINT NOT NULL COMMENT '1:Alipay 2:WeChat',
    status TINYINT NOT NULL DEFAULT 0,
    transaction_id VARCHAR(128),
    gateway_trade_no VARCHAR(128),
    payment_url VARCHAR(512),
    remark VARCHAR(512),
    paid_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no(order_no),
    INDEX idx_payment_no(payment_no),
    INDEX idx_transaction_id(transaction_id)
);

-- Callback logs
CREATE TABLE payment_callbacks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no VARCHAR(64) NOT NULL,
    gateway_type TINYINT NOT NULL,
    raw_data TEXT NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    processed BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_payment_no(payment_no)
);

-- Refund table
CREATE TABLE refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    refund_no VARCHAR(64) NOT NULL UNIQUE,
    refund_amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(512),
    status TINYINT NOT NULL DEFAULT 0,
    transaction_id VARCHAR(128),
    gateway_refund_no VARCHAR(128),
    refunded_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_id(payment_id),
    INDEX idx_refund_no(refund_no)
);

-- Refund callback logs
CREATE TABLE refund_callbacks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    refund_no VARCHAR(64) NOT NULL,
    gateway_type TINYINT NOT NULL,
    raw_data TEXT NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    processed BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_refund_no(refund_no)
);
```

## API Endpoints

### Payment Endpoints

```
POST   /api/v1/payments/create              - Create payment (return payment URL)
GET    /api/v1/payments/{id}                - Get payment by ID
GET    /api/v1/payments/no/{paymentNo}      - Get payment by number
POST   /api/v1/payments/callback/alipay     - Alipay async callback
POST   /api/v1/payments/callback/wechat     - WeChat async callback
POST   /api/v1/payments/query               - Query payment status from gateway
```

### Refund Endpoints

```
POST   /api/v1/refunds/create               - Create refund
GET    /api/v1/refunds/{id}                 - Get refund by ID
GET    /api/v1/refunds/no/{refundNo}        - Get refund by number
POST   /api/v1/refunds/callback/alipay      - Alipay refund callback
POST   /api/v1/refunds/callback/wechat      - WeChat refund callback
```

## Dependencies

Add to parent POM:

```xml
<alipay-sdk.version>4.38.157.ALL</alipay-sdk.version>
<okhttp.version>4.11.0</okhttp.version>

<dependency>
    <groupId>com.alipay.sdk</groupId>
    <artifactId>alipay-sdk-java</artifactId>
    <version>${alipay-sdk.version}</version>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>${okhttp.version}</version>
</dependency>
```

## Configuration

Application properties for payment gateways:

```yaml
alipay:
  app-id: ${ALIPAY_APP_ID}
  private-key: ${ALIPAY_PRIVATE_KEY}
  public-key: ${ALIPAY_PUBLIC_KEY}
  gateway-url: https://openapi.alipay.com/gateway.do
  notify-url: http://your-domain.com/api/v1/payments/callback/alipay
  return-url: http://your-domain.com/payment/result

wechat:
  app-id: ${WECHAT_APP_ID}
  mch-id: ${WECHAT_MCH_ID}
  api-key: ${WECHAT_API_KEY}
  cert-path: ${WECHAT_CERT_PATH}
  notify-url: http://your-domain.com/api/v1/payments/callback/wechat
```

## Testing Strategy

1. **Unit tests** - Test service logic without real gateway calls
2. **Integration tests** - Test with mock gateway responses
3. **Contract tests** - Verify OpenFeign client matches payment service API
4. **Sandbox testing** - Use Alipay/WeChat sandbox environments for end-to-end testing

## Security Considerations

1. RSA key pairs stored securely (environment variables or secret management)
2. All gateway callbacks must be signature-verified
3. Payment callbacks are idempotent (Redis locks)
4. Rate limiting on payment creation endpoints
5. Sensitive payment details never logged

## Migration Notes

- Existing `payments` table in `ccorder` database will remain for historical data
- New payments will be stored in `ccpay` database via new payment service
- Order Service will query new payment service for payment status
- Existing PaymentServiceImpl in Order Service will be deprecated