package com.cc.order.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.constant.OrderStatus;
import com.cc.common.dto.*;
import com.cc.common.exception.BusinessException;
import com.cc.order.mapper.*;
import com.cc.order.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String ORDER_LOCK_PREFIX = "order:lock:";
    private static final String INVENTORY_LOCK_PREFIX = "inventory:lock:";

    @Override
    @Transactional
    public Result<OrderDTO> createOrder(Long userId, CreateOrderRequest request) {
        // TODO: 获取收货地址信息
        // TODO: 验证订单项库存和价格
        // TODO: 扣减库存（使用分布式锁）

        // 生成订单号
        String orderNo = generateOrderNo();

        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            // TODO: 获取产品价格
            BigDecimal price = new BigDecimal("99.99"); // 临时值
            BigDecimal itemTotal = price.multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(OrderStatus.PAYMENT_PENDING);
        order.setReceiverName("收货人"); // TODO: 从地址获取
        order.setReceiverPhone("13800138000"); // TODO: 从地址获取
        order.setReceiverAddress("收货地址"); // TODO: 从地址获取

        orderMapper.insert(order);

        // 创建订单项
        List<OrderItem> items = request.getItems().stream().map(itemRequest -> {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemRequest.getProductId());
            item.setSkuId(itemRequest.getSkuId());
            item.setPrice(new BigDecimal("99.99")); // TODO: 获取实际价格
            item.setQuantity(itemRequest.getQuantity());
            item.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            return item;
        }).collect(Collectors.toList());

        for (OrderItem item : items) {
            orderItemMapper.insert(item);
        }

        // 记录状态历史
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(order.getId());
        history.setStatus(OrderStatus.PENDING);
        history.setRemark("订单创建");
        orderStatusHistoryMapper.insert(history);

        // 返回订单详情
        return Result.success(getOrderById(order.getId()).getData());
    }

    @Override
    public Result<OrderDTO> getOrderById(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        OrderDTO dto = toDTO(order);
        dto.setItems(orderItemMapper.findByOrderId(id).stream().map(this::toItemDTO).collect(Collectors.toList()));

        // 获取支付信息
        Payment payment = paymentMapper.findByOrderNo(order.getOrderNo());
        if (payment != null) {
            dto.setPayment(toPaymentDTO(payment));
        }

        return Result.success(dto);
    }

    @Override
    public Result<OrderDTO> getOrderByNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        return getOrderById(order.getId());
    }

    @Override
    public Result<PageResult<OrderDTO>> getUserOrders(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> orders = orderMapper.findByUserId(userId, offset, pageSize);
        int total = orderMapper.countByUserId(userId);

        List<OrderDTO> dtos = orders.stream().map(order -> {
            OrderDTO dto = toDTO(order);
            dto.setItems(orderItemMapper.findByOrderId(order.getId()).stream()
                    .map(this::toItemDTO).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        return Result.success(new PageResult<>((long)total, dtos, (long)pageNum, (long)pageSize));
    }

    @Override
    @Transactional
    public Result<Void> cancelOrder(Long id, Long userId) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS, "订单状态不允许取消");
        }

        // 更新订单状态
        orderMapper.updateStatus(id, OrderStatus.CANCELLED);

        // 记录状态历史
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(id);
        history.setStatus(OrderStatus.CANCELLED);
        history.setRemark("用户取消订单");
        orderStatusHistoryMapper.insert(history);

        // TODO: 恢复库存

        return Result.success();
    }

    @Override
    @Transactional
    public Result<PaymentDTO> processPayment(Long orderId, Integer paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS, "订单状态不允许支付");
        }
        if (order.getPaymentStatus() == OrderStatus.PAYMENT_SUCCESS) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID, "订单已支付");
        }

        // 创建支付单
        Result<PaymentDTO> result = paymentService.createPayment(orderId, paymentMethod);
        if (result.isSuccess()) {
            // 更新订单支付状态
            orderMapper.updatePaymentStatus(orderId, OrderStatus.PAYMENT_SUCCESS);
            orderMapper.updatePaymentTime(orderId, LocalDateTime.now());

            // 更新订单状态
            orderMapper.updateStatus(orderId, OrderStatus.PAID);

            // 记录状态历史
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrderId(orderId);
            history.setStatus(OrderStatus.PAID);
            history.setRemark("订单支付成功");
            orderStatusHistoryMapper.insert(history);
        }

        return result;
    }

    @Override
    @Transactional
    public Result<Void> updateOrderStatus(Long id, Integer status) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        orderMapper.updateStatus(id, status);

        // 记录状态历史
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(id);
        history.setStatus(status);
        history.setRemark("更新订单状态");
        orderStatusHistoryMapper.insert(history);

        return Result.success();
    }

    private String generateOrderNo() {
        // 格式: YYYYMMDD + 4位随机字符 + 6位序列号
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String sequence = String.format("%06d", System.currentTimeMillis() % 1000000);
        return date + random + sequence;
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setStatusText(getStatusText(order.getStatus()));
        dto.setPaymentTime(order.getPaymentTime() != null ? order.getPaymentTime().format(FORMATTER) : null);
        dto.setShippingTime(order.getShippingTime() != null ? order.getShippingTime().format(FORMATTER) : null);
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    private PaymentDTO toPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(payment, dto);
        dto.setPaymentMethodText(getPaymentMethodText(payment.getPaymentMethod()));
        dto.setStatusText(getPaymentStatusText(payment.getStatus()));
        return dto;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case OrderStatus.PENDING: return "待支付";
            case OrderStatus.PAID: return "已支付";
            case OrderStatus.SHIPPED: return "已发货";
            case OrderStatus.DELIVERED: return "已送达";
            case OrderStatus.COMPLETED: return "已完成";
            case OrderStatus.CANCELLED: return "已取消";
            case OrderStatus.REFUNDING: return "退款中";
            case OrderStatus.REFUNDED: return "已退款";
            default: return "未知";
        }
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