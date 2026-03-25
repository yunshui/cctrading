package com.cc.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品搜索请求DTO
 */
@Data
public class ProductSearchRequest {

    private String keyword;
    private Long categoryId;
    private List<Long> categoryIds;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer status;
    private Boolean isFeatured;
    private String sortBy; // price_asc, price_desc, sales, created_at
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}