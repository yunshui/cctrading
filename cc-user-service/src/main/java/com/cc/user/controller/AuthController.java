package com.cc.user.controller;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.user.model.User;
import com.cc.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        log.info("Login request for user: {}", username);
        return authService.login(username, password);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(ErrorCode.UNAUTHORIZED, "未认证");
        }
        return authService.getUserById(userId);
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    public Result<User> validateToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return authService.validateToken(token);
    }
}