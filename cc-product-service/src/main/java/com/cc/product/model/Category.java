package com.cc.product.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体
 */
@Data
public class Category {

    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}