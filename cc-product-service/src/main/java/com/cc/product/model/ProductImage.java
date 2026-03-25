package com.cc.product.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 产品图片实体
 */
@Data
public class ProductImage {

    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer isPrimary;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}