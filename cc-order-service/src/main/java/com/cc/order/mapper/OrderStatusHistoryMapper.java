package com.cc.order.mapper;

import com.cc.order.model.OrderStatusHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单状态历史Mapper
 */
@Mapper
public interface OrderStatusHistoryMapper {

    /**
     * 根据ID查询
     */
    OrderStatusHistory findById(@Param("id") Long id);

    /**
     * 查询订单的状态历史
     */
    List<OrderStatusHistory> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 插入状态历史
     */
    int insert(OrderStatusHistory history);
}