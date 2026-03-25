package com.cc.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品DTO
 */
@Data
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Boolean isFeatured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductImageDTO> images;
    private List<ProductSkuDTO> skus;
}