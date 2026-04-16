package com.cc.order.mapper;

import com.cc.order.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项Mapper
 */
@Mapper
public interface OrderItemMapper {

    /**
     * 根据ID查询订单项
     */
    OrderItem findById(@Param("id") Long id);

    /**
     * 查询订单的所有项
     */
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量插入订单项
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 插入订单项
     */
    int insert(OrderItem item);

    /**
     * 统计订单项数量
     */
    int countByOrderId(@Param("orderId") Long orderId);
}