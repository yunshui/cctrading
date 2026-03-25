package com.cc.user.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.common.util.AesUtil;
import com.cc.common.util.JwtUtil;
import com.cc.common.exception.BusinessException;
import com.cc.user.mapper.UserMapper;
import com.cc.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AesUtil aesUtil;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result<?> login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 2. 验证密码（使用AES解密后验证）
        try {
            String decryptedPassword = AesUtil.decrypt(user.getPassword(), user.getPasswordKey());
            if (!password.equals(decryptedPassword)) {
                return Result.error(ErrorCode.INVALID_PASSWORD, "密码错误");
            }
        } catch (Exception e) {
            log.error("Password decryption failed for user: {}", username, e);
            return Result.error(ErrorCode.INTERNAL_ERROR, "密码验证失败");
        }

        // 3. 检查账号状态
        if (user.getStatus() == 1) {
            return Result.error(ErrorCode.BAD_REQUEST, "账号已锁定");
        }

        // 4. 生成JWT Token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 5. 存储到Redis
        String redisKey = TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, 7, TimeUnit.DAYS);

        // 6. 更新最后登录时间
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now().format(FORMATTER));

        // 7. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        return Result.success(data);
    }

    @Override
    public Result<User> getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        // 清除敏感信息
        user.setPassword(null);
        user.setPasswordKey(null);
        return Result.success(user);
    }

    @Override
    public Result<User> validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token无效");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        return getUserById(userId);
    }
}