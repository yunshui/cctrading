package com.cc.product.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品SKU实体
 */
@Data
public class ProductSku {

    private Long id;
    private Long productId;
    private String skuCode;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String attributes;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}