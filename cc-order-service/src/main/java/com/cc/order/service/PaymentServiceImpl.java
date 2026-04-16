package com.cc.order.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.constant.OrderStatus;
import com.cc.common.dto.PaymentDTO;
import com.cc.common.dto.Result;
import com.cc.common.exception.BusinessException;
import com.cc.order.mapper.OrderMapper;
import com.cc.order.mapper.PaymentMapper;
import com.cc.order.model.Order;
import com.cc.order.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String PAYMENT_LOCK_PREFIX = "payment:lock:";

    @Override
    public Result<PaymentDTO> createPayment(Long orderId, Integer paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        // 检查是否已有支付记录
        Payment existingPayment = paymentMapper.findByOrderNo(order.getOrderNo());
        if (existingPayment != null && existingPayment.getStatus() == OrderStatus.PAYMENT_SUCCESS) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID, "订单已支付");
        }

        // 生成支付单号
        String paymentNo = generatePaymentNo();

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setPaymentNo(paymentNo);
        payment.setAmount(order.getActualAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(OrderStatus.PAYMENT_PENDING);

        paymentMapper.insert(payment);

        // TODO: 调用第三方支付接口（支付宝/微信）

        return Result.success(toDTO(payment));
    }

    @Override
    public Result<PaymentDTO> getPaymentById(Long id) {
        Payment payment = paymentMapper.findById(id);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "支付记录不存在");
        }
        return Result.success(toDTO(payment));
    }

    @Override
    public Result<PaymentDTO> getPaymentByOrderNo(String orderNo) {
        Payment payment = paymentMapper.findByOrderNo(orderNo);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "支付记录不存在");
        }
        return Result.success(toDTO(payment));
    }

    @Override
    public Result<Void> updatePaymentStatus(Long id, Integer status) {
        Payment payment = paymentMapper.findById(id);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "支付记录不存在");
        }

        paymentMapper.updateStatus(id, status);
        return Result.success();
    }

    @Override
    public Result<Void> handlePaymentCallback(String paymentNo, Integer status, String transactionId) {
        // 使用分布式锁防止重复处理
        String lockKey = PAYMENT_LOCK_PREFIX + paymentNo;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(300));

        if (locked == null || !locked) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "支付回调处理中，请勿重复提交");
        }

        try {
            Payment payment = paymentMapper.findByPaymentNo(paymentNo);
            if (payment == null) {
                throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "支付记录不存在");
            }

            if (payment.getStatus() == OrderStatus.PAYMENT_SUCCESS) {
                // 已处理，直接返回成功
                return Result.success();
            }

            // 更新支付状态
            paymentMapper.updateStatus(payment.getId(), status);
            paymentMapper.updateTransactionId(payment.getId(), transactionId);

            // TODO: 根据支付结果更新订单状态
            if (status == OrderStatus.PAYMENT_SUCCESS) {
                // 支付成功，更新订单状态
                orderMapper.updateStatus(payment.getOrderId(), OrderStatus.PAID);
                orderMapper.updatePaymentStatus(payment.getOrderId(), status);
                orderMapper.updatePaymentTime(payment.getOrderId(), LocalDateTime.now());
            }

            return Result.success();
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    private String generatePaymentNo() {
        // 格式: PAY + YYYYMMDDHHmmss + 6位随机字符
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PAY" + time + random;
    }

    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(payment, dto);
        dto.setPaymentMethodText(getPaymentMethodText(payment.getPaymentMethod()));
        dto.setStatusText(getPaymentStatusText(payment.getStatus()));
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    private String getPaymentMethodText(Integer method) {
        switch (method) {
            case 1: return "支付宝";
            case 2: return "微信支付";
            case 3: return "银行卡";
            default: return "未知";
        }
    }

    private String getPaymentStatusText(Integer status) {
        switch (status) {
            case OrderStatus.PAYMENT_PENDING: return "待支付";
            case OrderStatus.PAYMENT_SUCCESS: return "支付成功";
            case OrderStatus.PAYMENT_FAILED: return "支付失败";
            case OrderStatus.PAYMENT_REFUNDED: return "已退款";
            default: return "未知";
        }
    }
}