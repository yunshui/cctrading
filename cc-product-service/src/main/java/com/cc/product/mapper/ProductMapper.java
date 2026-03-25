package com.cc.product.mapper;

import com.cc.product.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper
 */
@Mapper
public interface ProductMapper {

    /**
     * 根据ID查询产品
     */
    Product findById(@Param("id") Long id);

    /**
     * 分页查询产品列表
     */
    List<Product> findList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据分类查询产品
     */
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 搜索产品
     */
    List<Product> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                        @Param("brand") String brand, @Param("minPrice") java.math.BigDecimal minPrice,
                        @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") Integer status,
                        @Param("isFeatured") Integer isFeatured, @Param("sortBy") String sortBy,
                        @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计搜索结果数量
     */
    int countSearch(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                   @Param("brand") String brand, @Param("minPrice") java.math.BigDecimal minPrice,
                   @Param("maxPrice") java.math.BigDecimal maxPrice, @Param("status") Integer status,
                   @Param("isFeatured") Integer isFeatured);

    /**
     * 插入产品
     */
    int insert(Product product);

    /**
     * 更新产品
     */
    int update(Product product);

    /**
     * 更新产品销量
     */
    int updateSales(@Param("id") Long id, @Param("sales") Integer sales);

    /**
     * 更新产品库存
     */
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    /**
     * 删除产品
     */
    int delete(@Param("id") Long id);

    /**
     * 统计分类下的产品数量
     */
    int countByCategoryId(@Param("categoryId") Long categoryId);
}