package com.cc.user.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String passwordKey;
    private Integer role;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}