package com.cc.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 产品SKU DTO
 */
@Data
public class ProductSkuDTO {

    private Long id;
    private Long productId;
    private String skuCode;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Map<String, Object> attributes;
    private Integer status;
}