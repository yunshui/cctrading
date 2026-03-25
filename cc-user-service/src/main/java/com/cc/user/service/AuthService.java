package com.cc.user.service;

import com.cc.common.dto.Result;
import com.cc.user.model.User;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    Result<?> login(String username, String password);

    /**
     * 根据ID获取用户信息
     */
    Result<User> getUserById(Long id);

    /**
     * 验证Token并返回用户信息
     */
    Result<User> validateToken(String token);
}