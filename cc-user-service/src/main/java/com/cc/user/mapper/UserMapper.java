package com.cc.user.mapper;

import com.cc.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 更新最后登录时间
     */
    int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") String lastLoginAt);
}