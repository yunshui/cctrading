package com.cc.common.constant;

/**
 * 订单状态常量
 */
public interface OrderStatus {

    // 订单状态
    int PENDING = 0;           // 待支付
    int PAID = 1;              // 已支付
    int SHIPPED = 2;           // 已发货
    int DELIVERED = 3;         // 已送达
    int COMPLETED = 4;         // 已完成
    int CANCELLED = 5;         // 已取消
    int REFUNDING = 6;         // 退款中
    int REFUNDED = 7;          // 已退款

    // 支付状态
    int PAYMENT_PENDING = 0;   // 待支付
    int PAYMENT_SUCCESS = 1;   // 支付成功
    int PAYMENT_FAILED = 2;    // 支付失败
    int PAYMENT_REFUNDED = 3;  // 已退款
}