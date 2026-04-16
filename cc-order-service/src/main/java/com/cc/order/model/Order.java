package com.cc.order.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
public class Order {

    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Integer paymentStatus;
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}