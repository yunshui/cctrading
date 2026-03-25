-- Create database
CREATE DATABASE IF NOT EXISTS ccproduct DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ccproduct;

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    level TINYINT DEFAULT 1,
    path VARCHAR(255) DEFAULT '',
    icon VARCHAR(255) DEFAULT '',
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1:active, 0:inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    brand VARCHAR(100) DEFAULT '',
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) DEFAULT 0,
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1:active, 0:inactive, 2:deleted',
    is_featured TINYINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_is_featured (is_featured),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product images table
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary TINYINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product SKUs table (for variants like size/color)
CREATE TABLE IF NOT EXISTS product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    attributes JSON DEFAULT NULL COMMENT '{"color":"red","size":"M"}',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sku_code (sku_code),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample categories
INSERT INTO categories (name, parent_id, level, path, sort_order) VALUES
('电子产品', 0, 1, '/1', 1),
('服装鞋帽', 0, 1, '/2', 2),
('食品饮料', 0, 1, '/3', 3),
('手机数码', 1, 2, '/1/4', 1),
('电脑配件', 1, 2, '/1/5', 2),
('男装', 2, 2, '/2/6', 1),
('女装', 2, 2, '/2/7', 2);

-- Insert sample products
INSERT INTO products (name, description, category_id, brand, price, original_price, stock, sales, status, is_featured) VALUES
('iPhone 15 Pro', '苹果最新旗舰手机，搭载A17芯片', 4, 'Apple', 7999.00, 8999.00, 100, 500, 1, 1),
('MacBook Pro', '14英寸M3芯片笔记本电脑', 5, 'Apple', 12999.00, 14999.00, 50, 200, 1, 1),
('男士休闲T恤', '纯棉材质，舒适透气', 6, 'Uniqlo', 99.00, 129.00, 500, 1000, 1, 0),
('女士连衣裙', '优雅设计，适合多种场合', 7, 'Zara', 299.00, 399.00, 200, 300, 1, 0);

-- Insert sample product images
INSERT INTO product_images (product_id, image_url, is_primary, sort_order) VALUES
(1, 'https://example.com/images/iphone15pro_1.jpg', 1, 1),
(1, 'https://example.com/images/iphone15pro_2.jpg', 0, 2),
(1, 'https://example.com/images/iphone15pro_3.jpg', 0, 3),
(2, 'https://example.com/images/macbookpro_1.jpg', 1, 1),
(2, 'https://example.com/images/macbookpro_2.jpg', 0, 2),
(3, 'https://example.com/images/tshirt_1.jpg', 1, 1),
(4, 'https://example.com/images/dress_1.jpg', 1, 1);

-- Insert sample product SKUs
INSERT INTO product_skus (product_id, sku_code, name, price, stock, attributes) VALUES
(1, 'IP15PRO-256-BLK', 'iPhone 15 Pro 256GB 黑色', 7999.00, 50, '{"color":"黑色","storage":"256GB"}'),
(1, 'IP15PRO-256-WHT', 'iPhone 15 Pro 256GB 白色', 7999.00, 30, '{"color":"白色","storage":"256GB"}'),
(1, 'IP15PRO-512-BLK', 'iPhone 15 Pro 512GB 黑色', 8999.00, 20, '{"color":"黑色","storage":"512GB"}'),
(2, 'MBP14-M3-16-512', 'MacBook Pro 14 M3 16GB 512GB', 12999.00, 25, '{"color":"深空灰","memory":"16GB","storage":"512GB"}'),
(2, 'MBP14-M3-16-1T', 'MacBook Pro 14 M3 16GB 1TB', 14999.00, 15, '{"color":"深空灰","memory":"16GB","storage":"1TB"}'),
(3, 'TSHIRT-M-BLK', '男士T恤 M 黑色', 99.00, 100, '{"size":"M","color":"黑色"}'),
(3, 'TSHIRT-L-BLK', '男士T恤 L 黑色', 99.00, 100, '{"size":"L","color":"黑色"}'),
(4, 'DRESS-S-RED', '女士连衣裙 S 红色', 299.00, 50, '{"size":"S","color":"红色"}'),
(4, 'DRESS-M-RED', '女士连衣裙 M 红色', 299.00, 50, '{"size":"M","color":"红色"}');