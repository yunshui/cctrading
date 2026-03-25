package com.cc.product.controller;

import com.cc.common.dto.CategoryDTO;
import com.cc.common.dto.Result;
import com.cc.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryDTO>> getCategoryTree() {
        log.info("Get category tree");
        return categoryService.getCategoryTree();
    }

    /**
     * 获取顶级分类
     */
    @GetMapping("/top")
    public Result<List<CategoryDTO>> getTopLevelCategories() {
        log.info("Get top level categories");
        return categoryService.getTopLevelCategories();
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    public Result<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("Get category by id: {}", id);
        return categoryService.getCategoryById(id);
    }

    /**
     * 根据父ID查询子分类
     */
    @GetMapping("/{parentId}/children")
    public Result<List<CategoryDTO>> getChildrenByParentId(@PathVariable Long parentId) {
        log.info("Get children by parent id: {}", parentId);
        return categoryService.getChildrenByParentId(parentId);
    }
}