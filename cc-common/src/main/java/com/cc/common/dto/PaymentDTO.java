package com.cc.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO {

    private Long id;
    private Long orderId;
    private String orderNo;
    private String paymentNo;
    private BigDecimal amount;
    private Integer paymentMethod;  // 1:支付宝 2:微信 3:银行卡
    private String paymentMethodText;
    private Integer status;
    private String statusText;
    private String transactionId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}