package com.cc.order.mapper;

import com.cc.order.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper {

    /**
     * 根据ID查询订单
     */
    Order findById(@Param("id") Long id);

    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询用户的订单列表
     */
    List<Order> findByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计用户订单数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 插入订单
     */
    int insert(Order order);

    /**
     * 更新订单
     */
    int update(Order order);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Integer status);

    /**
     * 更新支付时间
     */
    int updatePaymentTime(@Param("id") Long id, @Param("paymentTime") java.time.LocalDateTime paymentTime);
}