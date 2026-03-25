package com.cc.product.controller;

import com.cc.common.dto.PageResult;
import com.cc.common.dto.ProductDTO;
import com.cc.common.dto.ProductSearchRequest;
import com.cc.common.dto.Result;
import com.cc.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 产品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询产品列表
     */
    @GetMapping
    public Result<PageResult<ProductDTO>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("Get product list: pageNum={}, pageSize={}", pageNum, pageSize);
        return productService.getProductList(pageNum, pageSize);
    }

    /**
     * 根据ID查询产品详情
     */
    @GetMapping("/{id}")
    public Result<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("Get product by id: {}", id);
        return productService.getProductById(id);
    }

    /**
     * 搜索产品
     */
    @PostMapping("/search")
    public Result<PageResult<ProductDTO>> searchProducts(@RequestBody ProductSearchRequest request) {
        log.info("Search products: {}", request);
        return productService.searchProducts(request);
    }

    /**
     * 获取分类下的产品
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageResult<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("Get products by category: {}, pageNum={}, pageSize={}", categoryId, pageNum, pageSize);
        return productService.getProductsByCategory(categoryId, pageNum, pageSize);
    }

    /**
     * 获取特色产品
     */
    @GetMapping("/featured")
    public Result<PageResult<ProductDTO>> getFeaturedProducts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("Get featured products: pageNum={}, pageSize={}", pageNum, pageSize);
        return productService.getFeaturedProducts(pageNum, pageSize);
    }
}