package com.cc.product.mapper;

import com.cc.product.model.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper {

    /**
     * 根据ID查询SKU
     */
    ProductSku findById(@Param("id") Long id);

    /**
     * 根据SKU编码查询
     */
    ProductSku findBySkuCode(@Param("skuCode") String skuCode);

    /**
     * 查询产品的所有SKU
     */
    List<ProductSku> findByProductId(@Param("productId") Long productId);

    /**
     * 插入SKU
     */
    int insert(ProductSku productSku);

    /**
     * 更新SKU
     */
    int update(ProductSku productSku);

    /**
     * 更新SKU库存
     */
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    /**
     * 删除SKU
     */
    int delete(@Param("id") Long id);

    /**
     * 删除产品的所有SKU
     */
    int deleteByProductId(@Param("productId") Long productId);
}