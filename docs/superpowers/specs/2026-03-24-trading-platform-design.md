# 商品交易软件 - 设计文档

**日期**: 2026-03-24
**版本**: 1.0
**类型**: 微服务架构

---

## 1. 项目概述

### 1.1 背景

基于现有用户管理系统，构建一个面向个人消费者的混合模式（B2C + C2C）商品交易平台。

### 1.1.1 与现有系统的集成

用户服务将直接复用现有的用户认证和权限系统：
- 用户表结构保持不变
- 角色定义保持不变（0=普通用户，1=管理员）
- 新增用户地址表用于支持多地址管理
- 认证流程复用现有的JWT机制

### 1.2 目标

- 支持企业和个人用户作为卖家
- 支持实物和虚拟商品交易
- 对接真实支付渠道
- 采用微服务架构保证扩展性

### 1.3 技术选型

| 组件 | 技术选型 | 版本 |
|------|---------|------|
| 微服务框架 | Spring Cloud Alibaba | 2021.0.5.0 |
| 服务注册发现 | Nacos | 2.2.0 |
| 配置中心 | Nacos Config | 2.2.0 |
| API网关 | Spring Cloud Gateway | 3.1.8 |
| 服务调用 | OpenFeign | 3.1.8 |
| 负载均衡 | Spring Cloud LoadBalancer | 3.1.8 |
| 熔断降级 | Sentinel | 1.8.6 |
| 分布式事务 | Seata | 1.7.0 |
| 后端框架 | Spring Boot | 2.7.18 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |

---

## 2. 系统架构

### 2.1 整体架构图

```
                        ┌─────────────────┐
                        │   前端应用        │
                        │  (HTML/CSS/JS)   │
                        └────────┬─────────┘
                                 │ HTTP/HTTPS
                                 ▼
                        ┌─────────────────┐
                        │  API Gateway    │
                        │ Spring Cloud    │
                        │ Gateway         │
                        │  (Port: 8080)   │
                        └────────┬─────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
    ┌───────────────┐  ┌───────────────┐  ┌───────────────┐
    │  用户服务      │  │  商品服务      │  │  订单服务      │
    │ User Service  │  │ Product Svc    │  │ Order Service │
    │  (Port: 8081) │  │  (Port: 8082) │  │  (Port: 8083) │
    └───────────────┘  └───────────────┘  └───────────────┘
                │                │                │
                └────────────────┼────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │   Nacos         │
                        │ (注册+配置中心)  │
                        │   (Port: 8848)  │
                        └─────────────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
         ┌──────────┐    ┌──────────┐    ┌──────────┐
         │ 用户库   │    │ 商品库   │    │ 订单库   │
         │ ccuser   │    │ ccproduct│    │ ccorder  │
         └──────────┘    └──────────┘    └──────────┘
```

### 2.2 服务划分

| 服务名称 | 职责 | 端口 | 数据库 |
|---------|------|------|--------|
| Gateway | API网关，路由转发、认证鉴权 | 8080 | - |
| User Service | 用户注册、登录、信息管理 | 8081 | ccuser |
| Product Service | 商品管理、分类、搜索 | 8082 | ccproduct |
| Order Service | 订单创建、状态管理 | 8083 | ccorder |
| Payment Service | 支付处理 | 8084 | ccpayment |

**注意**：Payment Service 为 MVP 必需服务，负责对接真实支付渠道（支付宝、微信支付）。

### 2.3 服务间通信

- **同步通信**：使用 OpenFeign 进行服务间调用
- **异步通信**：使用消息队列（后续扩展）
- **API版本**：所有接口统一前缀 `/api/v1`

---

## 3. 数据库设计

### 3.1 用户服务数据库

#### 用户表 (users)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 用户ID，主键 | 自增 |
| username | varchar(20) | 用户名 | 唯一，非空 |
| password | varchar(255) | 密码（AES加密） | 非空 |
| password_key | varchar(255) | 密码加密密钥 | 非空 |
| role | tinyint | 账号级别（0=普通用户，1=管理员） | 非空，默认0 |
| phone | varchar(11) | 手机号 | 唯一，非空 |
| status | tinyint | 账号状态（0=正常，1=锁定） | 非空，默认0 |
| created_at | datetime | 注册时间 | 非空 |
| updated_at | datetime | 更新时间 | 非空 |
| last_login_at | datetime | 最后登录时间 | 可空 |

#### 用户地址表 (user_addresses)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 地址ID，主键 | 自增 |
| user_id | bigint | 用户ID | 非空 |
| receiver_name | varchar(50) | 收货人姓名 | 非空 |
| receiver_phone | varchar(11) | 收货人电话 | 非空 |
| province | varchar(50) | 省份 | 非空 |
| city | varchar(50) | 城市 | 非空 |
| district | varchar(50) | 区县 | 非空 |
| detail | varchar(200) | 详细地址 | 非空 |
| is_default | tinyint | 是否默认地址 | 非空，默认0 |
| created_at | datetime | 创建时间 | 非空 |
| updated_at | datetime | 更新时间 | 非空 |

**索引建议**：
- INDEX idx_user_id (user_id)
- INDEX idx_is_default (user_id, is_default)

**集成说明**：
- 用户地址表作为现有用户系统的扩展
- 使用现有users表的user_id作为外键
- 复用现有的用户认证和权限机制

### 1.1.2 认证架构升级

**现有系统**：使用Spring Session + Redis的Session管理

**新架构**：升级为JWT Token认证

**认证流程升级**：
1. User Service 保留现有的登录验证逻辑
2. 登录成功后生成JWT Token返回给客户端
3. 客户端后续请求携带JWT Token
4. Gateway验证Token有效性并转发到下游服务
5. 服务间调用通过Feign传递Token

**Token传递机制**：
- 客户端 → Gateway：Header `Authorization: Bearer <token>`
- Gateway → 服务：Header `X-User-Id`, `X-User-Name`, `X-User-Role`
- 服务间调用：Feign自动传递用户上下文信息

**平滑迁移策略**：
- 保留现有Session机制，逐步迁移到JWT
- 两套机制并存一段时间
- 监控使用情况，完全切换后下线Session机制

#### 操作日志表 (operation_logs)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 日志ID，主键 | 自增 |
| user_id | bigint | 操作用户ID | 非空 |
| username | varchar(20) | 操作用户名 | 非空 |
| operation_type | varchar(50) | 操作类型 | 非空 |
| operation_detail | text | 操作详情 | 可空 |
| ip_address | varchar(50) | IP地址 | 可空 |
| result | tinyint | 操作结果（0=失败，1=成功） | 非空 |
| created_at | datetime | 操作时间 | 非空 |

### 3.2 商品服务数据库

#### 商品表 (products)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 商品ID，主键 | 自增 |
| seller_id | bigint | 卖家用户ID | 非空 |
| name | varchar(100) | 商品名称 | 非空 |
| description | text | 商品描述 | 可空 |
| category_id | bigint | 分类ID | 非空 |
| price | decimal(10,2) | 价格 | 非空 |
| stock | int | 库存（实物商品） | 实物商品默认0，虚拟商品设为-1表示无库存限制 |
| type | tinyint | 商品类型（0=实物，1=虚拟） | 非空，默认0 |
| status | tinyint | 状态（0=上架，1=下架，2=已售出） | 非空，默认0 |
| image_urls | text | 图片URL列表(JSON) | 可空 |
| created_at | datetime | 创建时间 | 非空 |
| updated_at | datetime | 更新时间 | 非空 |

**索引建议**：
- INDEX idx_seller_id (seller_id)
- INDEX idx_category_id (category_id)
- INDEX idx_status (status)
- INDEX idx_type (type)
- INDEX idx_price (price)
- INDEX idx_created_at (created_at)

#### 商品分类表 (categories)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 分类ID，主键 | 自增 |
| name | varchar(50) | 分类名称 | 非空 |
| parent_id | bigint | 父分类ID | 可空 |
| sort_order | int | 排序顺序 | 默认0 |

### 3.3 订单服务数据库

#### 订单表 (orders)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 订单ID，主键 | 自增 |
| order_no | varchar(32) | 订单号 | 唯一，非空 |
| buyer_id | bigint | 买家ID | 非空 |
| seller_id | bigint | 卖家ID | 非空 |
| total_amount | decimal(10,2) | 订单总金额 | 非空 |
| payment_amount | decimal(10,2) | 实付金额 | 可空 |
| status | tinyint | 订单状态（0=待付款，1=待发货，2=待收货，3=已完成，4=已取消） | 非空，默认0 |
| pay_time | datetime | 支付时间 | 可空 |
| delivery_time | datetime | 发货时间 | 可空 |
| receive_time | datetime | 收货时间 | 可空 |
| address | json | 收货地址(JSON) | 可空 |
| remark | varchar(500) | 备注 | 可空 |
| created_at | datetime | 创建时间 | 非空 |
| updated_at | datetime | 更新时间 | 非空 |

**索引建议**：
- INDEX idx_order_no (order_no)
- INDEX idx_buyer_id (buyer_id)
- INDEX idx_seller_id (seller_id)
- INDEX idx_status (status)
- INDEX idx_created_at (created_at)

#### 订单明细表 (order_items)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 明细ID，主键 | 自增 |
| order_id | bigint | 订单ID | 非空 |
| product_id | bigint | 商品ID | 非空 |
| product_name | varchar(100) | 商品名称 | 非空 |
| product_price | decimal(10,2) | 商品单价 | 非空 |
| quantity | int | 购买数量 | 非空 |
| subtotal | decimal(10,2) | 小计 | 非空 |

**索引建议**：
- INDEX idx_order_id (order_id)
- INDEX idx_product_id (product_id)

### 3.4 支付服务数据库

#### 支付订单表 (payments)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 支付ID，主键 | 自增 |
| payment_no | varchar(32) | 支付单号 | 唯一，非空 |
| order_no | varchar(32) | 订单号 | 非空 |
| order_id | bigint | 订单ID | 非空 |
| user_id | bigint | 用户ID | 非空 |
| amount | decimal(10,2) | 支付金额 | 非空 |
| payment_method | tinyint | 支付方式（1=支付宝，2=微信） | 非空 |
| status | tinyint | 状态（0=待支付，1=支付中，2=支付成功，3=支付失败，4=已退款） | 非空，默认0 |
| third_party_no | varchar(64) | 第三方支付流水号 | 可空 |
| pay_time | datetime | 支付时间 | 可空 |
| expire_time | datetime | 过期时间 | 非空 |
| created_at | datetime | 创建时间 | 非空 |
| updated_at | datetime | 更新时间 | 非空 |

#### 支付回调表 (payment_callbacks)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 回调ID，主键 | 自增 |
| payment_no | varchar(32) | 支付单号 | 非空 |
| raw_data | text | 原始回调数据 | 非空 |
| processed | tinyint | 是否已处理 | 非空，默认0 |
| result | varchar(20) | 处理结果 | 可空 |
| created_at | datetime | 创建时间 | 非空 |

**索引建议**：
- INDEX idx_payment_no (payment_no)
- INDEX idx_order_no (order_no)
- INDEX idx_user_id (user_id)
- INDEX idx_status (status)

---

## 4. 核心业务流程

### 4.1 商品发布流程

```
用户登录 → 网关鉴权 → 商品服务创建商品
    ↓
生成商品信息（名称、价格、库存、类型、图片等）
    ↓
保存到数据库
    ↓
返回商品ID
```

### 4.2 下单购买流程

```
用户登录 → 网关鉴权 → 加入购物车（前端）
    ↓
确认订单 → 订单服务创建订单
    ↓
调用商品服务扣减库存（分布式锁）
    ↓
库存扣减成功 → 订单状态：待付款
    ↓
调用支付服务 → 用户支付
    ↓
支付成功 → 订单状态：待发货
```

### 4.3 购物车设计

**前端管理**：购物车数据存储在前端（localStorage），无需后端持久化。

**数据结构**：
```json
{
  "cart": [
    {
      "productId": 123,
      "sellerId": 456,
      "name": "商品名称",
      "price": 99.99,
      "quantity": 1,
      "type": 0,
      "imageUrl": "http://example.com/image.jpg"
    }
  ],
  "version": "v1",
  "timestamp": 1648123456789
}
```

**失效规则**：
- 购物车数据有效期24小时
- 每次操作延长有效期
- 过期后清空本地数据

**后端验证**：下单时需重新验证商品状态（是否下架、库存是否充足、价格是否变化），验证失败则拒绝下单并提示用户刷新购物车。

**多设备同步**：当前版本不支持跨设备购物车同步，如需同步需要后端持久化（可后期扩展）。

### 4.4 订单号生成规则

**格式**：`YYYYMMDD` + 4位随机字符 + 6位序列号（16位）

**示例**：`20260324A3B4C500001`

**防冲突策略**：
- 使用Redis的INCR原子操作生成序列号
- 每日重置序列号
- 4位随机字符分散同一时间段的请求

**幂等性保证**：
- 订单号唯一索引
- 创建订单前检查订单号是否存在
- 支持根据订单号查询状态

### 4.5 虚拟商品交付

#### 4.5.1 服务归属

虚拟商品交付功能归属于 **Order Service**，API路径为 `/api/v1/orders/virtual/*`。

#### 4.5.2 交付时机

订单状态变更为"已完成"后自动触发交付流程。

#### 4.5.3 数据模型

#### 虚拟资产表 (virtual_assets)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 资产ID，主键 | 自增 |
| order_id | bigint | 订单ID | 唯一，非空 |
| product_id | bigint | 商品ID | 非空 |
| asset_type | tinyint | 资产类型（1=兑换码，2=下载链接，3=权限） | 非空 |
| asset_data | text | 资产数据（JSON） | 非空 |
| deliver_time | datetime | 交付时间 | 非空 |
| expire_time | datetime | 过期时间（下载链接） | 可空 |
| status | tinyint | 状态（0=已交付，1=已使用，2=已过期） | 非空，默认0 |

#### 4.5.3 交付流程

1. **检测虚拟商品**：订单完成时检查订单明细中的商品类型
2. **生成资产**：根据商品类型调用对应的资产生成服务
3. **存储资产**：将资产信息写入virtual_assets表
4. **发送通知**：通过站内消息通知用户查看订单详情获取资产

#### 4.5.4 API接口

虚拟商品交付接口由Order Service提供，路径前缀为 `/api/v1/orders/virtual/`：

**生成兑换码**：`POST /api/v1/orders/virtual/generate-code`
- 请求参数：`{orderId, productId, quantity}`
- 返回：兑换码列表

**生成下载链接**：`POST /api/v1/orders/virtual/generate-link`
- 请求参数：`{orderId, productId, fileId}`
- 返回：下载链接和有效期

**开通权限**：`POST /api/v1/orders/virtual/grant-permission`
- 请求参数：`{userId, productId, resourceType}`
- 返回：权限开通状态

### 4.6 支付渠道集成

#### 4.6.1 支付宝集成

**API端点**：`https://openapi.alipay.com/gateway.do`

**认证方式**：RSA2签名

**回调URL**：`https://yourdomain.com/api/v1/payment/alipay/notify`

**关键接口**：
- 创建支付订单：`alipay.trade.page.pay`（网页支付）
- 查询订单状态：`alipay.trade.query`
- 申请退款：`alipay.trade.refund`

**配置项**：
- 应用ID（appId）
- 应用私钥
- 支付宝公钥
- 网关地址

#### 4.6.2 微信支付集成

**API端点**：`https://api.mch.weixin.qq.com/`

**认证方式**：API证书

**回调URL**：`https://yourdomain.com/api/v1/payment/wechat/notify`

**关键接口**：
- 创建支付订单：`/pay/unifiedorder`（统一下单）
- 查询订单状态：`/pay/orderquery`
- 申请退款：`/secapi/pay/refund`

**配置项**：
- 商户号（mch_id）
- 商户密钥
- API证书（退款功能需要）
- APPID

#### 4.6.3 凭证管理

**开发环境**：
- 使用支付宝沙箱环境
- 使用微信支付测试商户号
- 配置文件存储凭证

**生产环境**：
- 使用配置中心（Nacos）管理凭证
- 定期轮换API密钥
- 支付渠道回调IP白名单配置

### 4.7 限流实现

| 调用方 | 被调用方 | 场景 |
|-------|---------|------|
| Order Service | User Service | 获取用户信息、验证买家/卖家 |
| Order Service | Product Service | 获取商品信息、扣减库存 |
| Order Service | Payment Service | 发起支付、查询支付状态 |
| Gateway | 所有服务 | 路由转发、认证鉴权 |

---

## 5. 错误处理与安全性

### 5.1 统一异常处理

每个服务实现全局异常处理器，处理：
- 业务异常
- 参数校验异常
- 系统异常

### 5.2 错误码规范

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 1001-1999 | 用户服务错误 |
| 2001-2999 | 商品服务错误 |
| 3001-3999 | 订单服务错误 |
| 4001-4999 | 支付服务错误 |

### 5.3 安全措施

| 安全项 | 实现方式 |
|-------|---------|
| 服务间认证 | JWT Token + Spring Security |
| API安全 | Gateway统一鉴权 |
| 数据加密 | 密码AES加密，敏感字段加密传输 |
| 防刷限流 | Gateway限流策略 |
| 幂等性 | 订单号唯一，关键操作幂等校验 |

#### 5.3.1 限流策略

**按IP限流**：
- 全局：每IP 100次/分钟
- 登录接口：每IP 5次/分钟
- 注册接口：每IP 3次/分钟

**按用户限流**：
- 已登录用户：每用户 200次/分钟
- 下单接口：每用户 10次/分钟
- 商品发布：每用户 5次/分钟

**接口特定限流**：
- 支付接口：全局 1000次/分钟
- 搜索接口：每用户 50次/分钟

**限流响应**：
- HTTP 429 Too Many Requests
- 响应头 `X-RateLimit-Limit` 和 `X-RateLimit-Remaining`

---

## 6. 测试策略

### 6.1 测试层级

| 层级 | 测试类型 | 工具 | 覆盖范围 |
|-----|---------|------|---------|
| 单元测试 | Service层逻辑 | JUnit + Mockito | 业务逻辑 |
| 集成测试 | API接口 | TestRestTemplate + MockMvc | 控制器层 |
| 服务测试 | 服务间调用 | Testcontainers + WireMock | 完整流程 |
| E2E测试 | 端到端流程 | Selenium/Playwright | 用户操作 |

### 6.2 关键测试场景

**用户服务测试：**
- 用户注册（正常、重复用户名、手机号）
- 用户登录（正常、密码错误、验证码错误）
- 权限验证

**商品服务测试：**
- 商品发布（实物/虚拟）
- 库存扣减（并发场景）
- 商品搜索

**订单服务测试：**
- 订单创建
- 库存不足处理
- 支付回调处理
- 订单状态流转

**分布式事务测试：**
- 订单创建成功但库存扣减失败
- 支付成功但订单状态更新失败

---

## 7. 项目结构

### 7.1 整体目录结构

```
cctrading-platform/
├── cc-gateway/              # API网关服务
│   ├── src/main/java/com/cc/gateway/
│   └── pom.xml
├── cc-user-service/         # 用户服务
│   ├── src/main/java/com/cc/user/
│   └── pom.xml
├── cc-product-service/      # 商品服务
│   ├── src/main/java/com/cc/product/
│   └── pom.xml
├── cc-order-service/        # 订单服务
│   ├── src/main/java/com/cc/order/
│   └── pom.xml
├── cc-payment-service/      # 支付服务
│   ├── src/main/java/com/cc/payment/
│   └── pom.xml
├── cc-common/               # 公共模块
│   ├── src/main/java/com/cc/common/
│   │   ├── constant/       # 常量
│   │   ├── exception/      # 异常
│   │   ├── util/          # 工具类
│   │   └── dto/           # 通用DTO
│   └── pom.xml
├── cc-frontend/            # 前端应用
│   ├── static/
│   │   ├── pages/
│   │   ├── css/
│   │   └── js/
│   └── index.html
├── docs/                   # 文档
│   └── database/
├── docker/                 # Docker配置
│   └── docker-compose.yml
└── pom.xml                # 父POM
```

### 7.2 公共模块

包含所有服务共享的代码：
- 统一响应结构 `Result<T>`
- 统一异常类 `BusinessException`
- 通用工具类
- OpenFeign客户端接口

---

## 8. 部署架构

### 8.1 开发环境

- 单机部署，所有服务运行在同一台机器
- 使用本地数据库和Redis
- Nacos单机模式

### 8.2 生产环境

- 使用Docker容器化部署
- Nacos集群模式（3节点）
- Redis主从复制
- MySQL主从复制
- 通过Kubernetes编排

---

## 9. 后续扩展

### 9.1 短期扩展

- 消息队列集成（RocketMQ）
- 搜索服务（Elasticsearch）
- 消息通知服务

### 9.2 长期扩展

- 推荐系统
- 数据分析平台
- 多端适配（移动端APP）

---

## 10. 风险与挑战

### 10.1 技术风险

- 分布式事务处理复杂度
- 服务间通信延迟
- 数据一致性问题

### 10.2 运维挑战

- 服务监控和告警
- 日志收集和分析
- 问题定位和故障排查

### 10.3 缓解措施

- 引入链路追踪（SkyWalking）
- 完善的监控体系（Prometheus + Grafana）
- 自动化测试和部署