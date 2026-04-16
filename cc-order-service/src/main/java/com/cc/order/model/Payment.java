package com.cc.order.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体
 */
@Data
public class Payment {

    private Long id;
    private Long orderId;
    private String orderNo;
    private String paymentNo;
    private BigDecimal amount;
    private Integer paymentMethod;
    private Integer status;
    private String transactionId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}