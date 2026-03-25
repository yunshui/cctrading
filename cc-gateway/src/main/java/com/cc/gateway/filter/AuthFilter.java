package com.cc.gateway.filter;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.Result;
import com.cc.common.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<String> WHITELIST = Arrays.asList(
            "/api/v1/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单路径直接放行
        if (isWhitelistPath(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "未提供认证信息");
        }

        String token = authorization.substring(7);

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 将用户信息放入请求头
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Name", username)
                .header("X-User-Role", String.valueOf(role))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isWhitelistPath(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.error(ErrorCode.UNAUTHORIZED, message);
        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to write unauthorized response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}