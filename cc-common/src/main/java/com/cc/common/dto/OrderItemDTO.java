package com.cc.common.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项DTO
 */
@Data
public class OrderItemDTO {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private Long skuId;
    private String skuCode;
    private String skuAttributes;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}