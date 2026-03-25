package com.cc.product.mapper;

import com.cc.product.model.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品图片Mapper
 */
@Mapper
public interface ProductImageMapper {

    /**
     * 根据ID查询产品图片
     */
    ProductImage findById(@Param("id") Long id);

    /**
     * 查询产品的所有图片
     */
    List<ProductImage> findByProductId(@Param("productId") Long productId);

    /**
     * 查询产品的主图
     */
    ProductImage findPrimaryByProductId(@Param("productId") Long productId);

    /**
     * 插入产品图片
     */
    int insert(ProductImage productImage);

    /**
     * 更新产品图片
     */
    int update(ProductImage productImage);

    /**
     * 删除产品图片
     */
    int delete(@Param("id") Long id);

    /**
     * 删除产品的所有图片
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 取消产品的主图
     */
    int clearPrimary(@Param("productId") Long productId);
}