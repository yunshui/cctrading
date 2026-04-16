package com.cc.order.mapper;

import com.cc.order.model.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付Mapper
 */
@Mapper
public interface PaymentMapper {

    /**
     * 根据ID查询支付
     */
    Payment findById(@Param("id") Long id);

    /**
     * 根据支付单号查询
     */
    Payment findByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单号查询支付
     */
    Payment findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 插入支付记录
     */
    int insert(Payment payment);

    /**
     * 更新支付记录
     */
    int update(Payment payment);

    /**
     * 更新支付状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新交易流水号
     */
    int updateTransactionId(@Param("id") Long id, @Param("transactionId") String transactionId);
}