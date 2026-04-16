package com.cc.order.service;

import com.cc.common.dto.PaymentDTO;
import com.cc.common.dto.Result;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付单
     */
    Result<PaymentDTO> createPayment(Long orderId, Integer paymentMethod);

    /**
     * 查询支付状态
     */
    Result<PaymentDTO> getPaymentById(Long id);

    /**
     * 根据订单号查询支付
     */
    Result<PaymentDTO> getPaymentByOrderNo(String orderNo);

    /**
     * 更新支付状态
     */
    Result<Void> updatePaymentStatus(Long id, Integer status);

    /**
     * 处理支付回调
     */
    Result<Void> handlePaymentCallback(String paymentNo, Integer status, String transactionId);
}