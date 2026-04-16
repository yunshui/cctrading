package com.cc.order.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态历史实体
 */
@Data
public class OrderStatusHistory {

    private Long id;
    private Long orderId;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}