-- Create database
CREATE DATABASE IF NOT EXISTS ccorder DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ccorder;

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    actual_amount DECIMAL(10,2) NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0:pending,1:paid,2:shipped,3:delivered,4:completed,5:cancelled,6:refunding,7:refunded',
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(500) NOT NULL,
    payment_status TINYINT DEFAULT 0 COMMENT '0:pending,1:success,2:failed,3:refunded',
    payment_time DATETIME DEFAULT NULL,
    shipping_time DATETIME DEFAULT NULL,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_image VARCHAR(500) DEFAULT '',
    sku_id BIGINT,
    sku_code VARCHAR(100) DEFAULT '',
    sku_attributes VARCHAR(500) DEFAULT '',
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    payment_no VARCHAR(64) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method TINYINT NOT NULL COMMENT '1:alipay,2:wechat,3:bank',
    status TINYINT DEFAULT 0 COMMENT '0:pending,1:success,2:failed,3:refunded',
    transaction_id VARCHAR(128) DEFAULT '',
    remark VARCHAR(255) DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_payment_no (payment_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order status history table
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status TINYINT NOT NULL,
    remark VARCHAR(255) DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample orders
INSERT INTO orders (user_id, order_no, total_amount, discount_amount, actual_amount, status, receiver_name, receiver_phone, receiver_address, payment_status) VALUES
(1, 'ORD20250325001', 7999.00, 0, 7999.00, 1, '张三', '13800138000', '北京市朝阳区xxx路xxx号', 1),
(1, 'ORD20250325002', 1299.00, 0, 1299.00, 0, '张三', '13800138000', '北京市朝阳区xxx路xxx号', 0);

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, product_name, product_image, sku_id, sku_code, sku_attributes, price, quantity, total_price) VALUES
(1, 1, 'iPhone 15 Pro', 'https://example.com/images/iphone15pro_1.jpg', 1, 'IP15PRO-256-BLK', '{"color":"黑色","storage":"256GB"}', 7999.00, 1, 7999.00),
(2, 3, '男士休闲T恤', 'https://example.com/images/tshirt_1.jpg', 6, 'TSHIRT-M-BLK', '{"size":"M","color":"黑色"}', 99.00, 1, 99.00),
(2, 4, '女士连衣裙', 'https://example.com/images/dress_1.jpg', 8, 'DRESS-S-RED', '{"size":"S","color":"红色"}', 299.00, 1, 299.00),
(2, 4, '女士连衣裙', 'https://example.com/images/dress_1.jpg', 9, 'DRESS-M-RED', '{"size":"M","color":"红色"}', 299.00, 3, 897.00);

-- Insert sample payments
INSERT INTO payments (order_id, order_no, payment_no, amount, payment_method, status, transaction_id) VALUES
(1, 'ORD20250325001', 'PAY20250325001', 7999.00, 1, 1, 'ALIPAY202503250001');