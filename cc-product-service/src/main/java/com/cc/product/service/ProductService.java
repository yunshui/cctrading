package com.cc.product.service;

import com.cc.common.dto.PageResult;
import com.cc.common.dto.ProductDTO;
import com.cc.common.dto.ProductSearchRequest;
import com.cc.common.dto.Result;

/**
 * 产品服务接口
 */
public interface ProductService {

    /**
     * 分页查询产品列表
     */
    Result<PageResult<ProductDTO>> getProductList(Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询产品详情
     */
    Result<ProductDTO> getProductById(Long id);

    /**
     * 搜索产品
     */
    Result<PageResult<ProductDTO>> searchProducts(ProductSearchRequest request);

    /**
     * 获取分类下的产品
     */
    Result<PageResult<ProductDTO>> getProductsByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 获取特色产品
     */
    Result<PageResult<ProductDTO>> getFeaturedProducts(Integer pageNum, Integer pageSize);
}