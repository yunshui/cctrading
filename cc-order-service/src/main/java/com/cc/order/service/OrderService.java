package com.cc.order.service;

import com.cc.common.constant.OrderStatus;
import com.cc.common.dto.*;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    Result<OrderDTO> createOrder(Long userId, CreateOrderRequest request);

    /**
     * 根据ID查询订单
     */
    Result<OrderDTO> getOrderById(Long id);

    /**
     * 根据订单号查询订单
     */
    Result<OrderDTO> getOrderByNo(String orderNo);

    /**
     * 查询用户的订单列表
     */
    Result<PageResult<OrderDTO>> getUserOrders(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 取消订单
     */
    Result<Void> cancelOrder(Long id, Long userId);

    /**
     * 处理支付
     */
    Result<PaymentDTO> processPayment(Long orderId, Integer paymentMethod);

    /**
     * 更新订单状态
     */
    Result<Void> updateOrderStatus(Long id, Integer status);
}