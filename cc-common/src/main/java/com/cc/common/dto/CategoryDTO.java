package com.cc.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类DTO
 */
@Data
public class CategoryDTO {

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
    private List<CategoryDTO> children;
    private Integer productCount;
}