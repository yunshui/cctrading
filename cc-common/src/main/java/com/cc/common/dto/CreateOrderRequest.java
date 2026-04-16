package com.cc.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {

    private Long addressId;
    private List<OrderItemRequest> items;
    private String remark;
    private Long couponId;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Long skuId;
        private Integer quantity;
    }
}