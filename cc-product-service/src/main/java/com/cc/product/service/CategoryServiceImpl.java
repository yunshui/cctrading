package com.cc.product.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.CategoryDTO;
import com.cc.common.dto.Result;
import com.cc.product.mapper.CategoryMapper;
import com.cc.product.mapper.ProductMapper;
import com.cc.product.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CATEGORY_CACHE_PREFIX = "category:";
    private static final String CATEGORY_TREE_CACHE = "category:tree";
    private static final int CACHE_EXPIRE_HOURS = 2;

    @Override
    public Result<List<CategoryDTO>> getCategoryTree() {
        // 先尝试从缓存获取
        List<CategoryDTO> cached = (List<CategoryDTO>) redisTemplate.opsForValue().get(CATEGORY_TREE_CACHE);
        if (cached != null) {
            return Result.success(cached);
        }

        List<Category> categories = categoryMapper.findTree();
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();

        // 先创建所有分类节点
        for (Category category : categories) {
            CategoryDTO dto = toDTO(category);
            dto.setChildren(new ArrayList<>());
            categoryMap.put(dto.getId(), dto);
        }

        // 构建树结构
        List<CategoryDTO> rootCategories = new ArrayList<>();
        for (CategoryDTO dto : categoryMap.values()) {
            if (dto.getParentId() == 0) {
                rootCategories.add(dto);
            } else {
                CategoryDTO parent = categoryMap.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        // 缓存结果
        redisTemplate.opsForValue().set(CATEGORY_TREE_CACHE, rootCategories, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return Result.success(rootCategories);
    }

    @Override
    public Result<List<CategoryDTO>> getTopLevelCategories() {
        List<Category> categories = categoryMapper.findTopLevel();
        List<CategoryDTO> dtos = categories.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(dtos);
    }

    @Override
    public Result<CategoryDTO> getCategoryById(Long id) {
        String cacheKey = CATEGORY_CACHE_PREFIX + id;
        CategoryDTO cached = (CategoryDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Result.success(cached);
        }

        Category category = categoryMapper.findById(id);
        if (category == null) {
            return Result.error(ErrorCode.NOT_FOUND, "分类不存在");
        }

        CategoryDTO dto = toDTO(category);
        dto.setProductCount(productMapper.countByCategoryId(id));
        redisTemplate.opsForValue().set(cacheKey, dto, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return Result.success(dto);
    }

    @Override
    public Result<List<CategoryDTO>> getChildrenByParentId(Long parentId) {
        List<Category> categories = categoryMapper.findByParentId(parentId);
        List<CategoryDTO> dtos = categories.stream().map(cat -> {
            CategoryDTO dto = toDTO(cat);
            dto.setProductCount(productMapper.countByCategoryId(cat.getId()));
            return dto;
        }).collect(Collectors.toList());
        return Result.success(dtos);
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}