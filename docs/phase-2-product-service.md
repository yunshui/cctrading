# Phase 2: Product Service

## Overview

Phase 2 implements a comprehensive product management system including product catalog, categories, inventory tracking, and search capabilities.

## Features

- Product CRUD operations
- Category tree management
- Product search with filters
- Redis caching for performance
- Product images management
- SKU (Stock Keeping Unit) support for product variants

## API Endpoints

### Products

- `GET /api/v1/products` - List products with pagination
- `GET /api/v1/products/{id}` - Get product details
- `POST /api/v1/products/search` - Search products
- `GET /api/v1/products/category/{categoryId}` - Get products by category
- `GET /api/v1/products/featured` - Get featured products

### Categories

- `GET /api/v1/categories/tree` - Get category tree
- `GET /api/v1/categories/top` - Get top-level categories
- `GET /api/v1/categories/{id}` - Get category by ID
- `GET /api/v1/categories/{parentId}/children` - Get child categories

## Quick Start

### 1. Setup Database

```bash
mysql -u root -p < docs/database/ccproduct_schema.sql
```

### 2. Build Project

```bash
mvn clean package
```

### 3. Start Services

```bash
# Start infrastructure
docker-compose up -d mysql redis nacos

# Start product service
java -jar cc-product-service/target/cc-product-service-1.0.0.jar
```

### 4. Test APIs

Get category tree:
```bash
curl http://localhost:8082/api/v1/categories/tree
```

Get featured products:
```bash
curl http://localhost:8082/api/v1/products/featured?pageNum=1&pageSize=10
```

Search products:
```bash
curl -X POST http://localhost:8082/api/v1/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "iPhone",
    "pageNum": 1,
    "pageSize": 10
  }'
```

Get product details:
```bash
curl http://localhost:8082/api/v1/products/1
```

## Database Schema

### Products Table
- `id` - Primary key
- `name` - Product name
- `description` - Product description
- `category_id` - Category reference
- `price` - Current price
- `original_price` - Original price
- `stock` - Available stock
- `sales` - Sales count
- `status` - Product status (1:active, 0:inactive, 2:deleted)
- `is_featured` - Whether featured (1:true, 0:false)

### Categories Table
- `id` - Primary key
- `name` - Category name
- `parent_id` - Parent category (0 for top-level)
- `level` - Category level (1, 2, 3...)
- `path` - Category path for sorting (e.g., /1/4/)

### Product Images Table
- `id` - Primary key
- `product_id` - Product reference
- `image_url` - Image URL
- `is_primary` - Whether primary image (1:true, 0:false)

### Product SKUs Table
- `id` - Primary key
- `product_id` - Product reference
- `sku_code` - Unique SKU code
- `attributes` - JSON attributes (e.g., {"color":"red","size":"M"})

## Caching Strategy

Product data is cached in Redis for improved performance:

- Product details: cached for 1 hour
- Category tree: cached for 2 hours
- Cache keys: `product:{id}`, `category:{id}`, `category:tree`

## Next Steps

Proceed to Phase 3: Order Service implementation