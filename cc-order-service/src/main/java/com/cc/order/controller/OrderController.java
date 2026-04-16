package com.cc.order.controller;

import com.cc.common.constant.OrderStatus;
import com.cc.common.dto.*;
import com.cc.order.service.OrderService;
import com.cc.order.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderDTO> createOrder(@RequestHeader("X-User-Id") Long userId,
                                       @RequestBody CreateOrderRequest request) {
        log.info("Create order for user: {}, items: {}", userId, request.getItems().size());
        return orderService.createOrder(userId, request);
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrderById(@PathVariable Long id) {
        log.info("Get order by id: {}", id);
        return orderService.getOrderById(id);
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/no/{orderNo}")
    public Result<OrderDTO> getOrderByNo(@PathVariable String orderNo) {
        log.info("Get order by no: {}", orderNo);
        return orderService.getOrderByNo(orderNo);
    }

    /**
     * 查询用户的订单列表
     */
    @GetMapping
    public Result<PageResult<OrderDTO>> getUserOrders(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("Get orders for user: {}, page: {}", userId, pageNum);
        return orderService.getUserOrders(userId, pageNum, pageSize);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id,
                                   @RequestHeader("X-User-Id") Long userId) {
        log.info("Cancel order: {} by user: {}", id, userId);
        return orderService.cancelOrder(id, userId);
    }

    /**
     * 处理支付
     */
    @PostMapping("/{id}/pay")
    public Result<PaymentDTO> processPayment(@PathVariable Long id,
                                           @RequestParam(defaultValue = "1") Integer paymentMethod) {
        log.info("Process payment for order: {}, method: {}", id, paymentMethod);
        return orderService.processPayment(id, paymentMethod);
    }

    /**
     * 获取订单状态
     */
    @GetMapping("/{id}/status")
    public Result<OrderDTO> getOrderStatus(@PathVariable Long id) {
        log.info("Get order status for order: {}", id);
        return orderService.getOrderById(id);
    }

    /**
     * 支付回调
     */
    @PostMapping("/payment/callback")
    public Result<Void> handlePaymentCallback(@RequestParam String paymentNo,
                                              @RequestParam Integer status,
                                              @RequestParam String transactionId) {
        log.info("Handle payment callback: paymentNo={}, status={}, transactionId={}",
                 paymentNo, status, transactionId);
        return paymentService.handlePaymentCallback(paymentNo, status, transactionId);
    }
}