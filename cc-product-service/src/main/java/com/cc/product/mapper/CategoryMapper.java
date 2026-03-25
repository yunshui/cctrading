package com.cc.product.mapper;

import com.cc.product.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    /**
     * 根据ID查询分类
     */
    Category findById(@Param("id") Long id);

    /**
     * 查询所有分类
     */
    List<Category> findAll();

    /**
     * 查询顶级分类
     */
    List<Category> findTopLevel();

    /**
     * 根据父ID查询子分类
     */
    List<Category> findByParentId(@Param("parentId") Long parentId);

    /**
     * 根据路径查询分类树
     */
    List<Category> findTree();

    /**
     * 插入分类
     */
    int insert(Category category);

    /**
     * 更新分类
     */
    int update(Category category);

    /**
     * 删除分类
     */
    int delete(@Param("id") Long id);

    /**
     * 统计分类下的子分类数量
     */
    int countChildren(@Param("parentId") Long parentId);
}