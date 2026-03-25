package com.cc.common.dto;

import lombok.Data;

/**
 * 产品图片DTO
 */
@Data
public class ProductImageDTO {

    private Long id;
    private Long productId;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer sortOrder;
}