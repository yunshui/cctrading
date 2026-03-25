package com.cc.product.service;

import com.cc.common.dto.*;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 获取分类树
     */
    Result<List<CategoryDTO>> getCategoryTree();

    /**
     * 获取顶级分类
     */
    Result<List<CategoryDTO>> getTopLevelCategories();

    /**
     * 根据ID查询分类
     */
    Result<CategoryDTO> getCategoryById(Long id);

    /**
     * 根据父ID查询子分类
     */
    Result<List<CategoryDTO>> getChildrenByParentId(Long parentId);
}