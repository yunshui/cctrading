package com.cc.product.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.*;
import com.cc.product.mapper.*;
import com.cc.product.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 产品服务实现
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final int CACHE_EXPIRE_HOURS = 1;

    @Override
    public Result<PageResult<ProductDTO>> getProductList(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findList(offset, pageSize);
        int total = products.size(); // 简化处理

        List<ProductDTO> dtos = products.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(new PageResult<>((long)total, dtos, (long)pageNum, (long)pageSize));
    }

    @Override
    public Result<ProductDTO> getProductById(Long id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductDTO cached = (ProductDTO) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return Result.success(cached);
        }

        Product product = productMapper.findById(id);
        if (product == null) {
            return Result.error(ErrorCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        ProductDTO dto = toDTO(product);
        redisTemplate.opsForValue().set(cacheKey, dto, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return Result.success(dto);
    }

    @Override
    public Result<PageResult<ProductDTO>> searchProducts(ProductSearchRequest request) {
        int offset = (request.getPageNum() - 1) * request.getPageSize();

        String keyword = request.getKeyword();
        Long categoryId = request.getCategoryId();
        String brand = request.getBrand();
        BigDecimal minPrice = request.getMinPrice();
        BigDecimal maxPrice = request.getMaxPrice();
        Integer status = request.getStatus() != null ? request.getStatus() : 1;
        Integer isFeatured = request.getIsFeatured() != null && request.getIsFeatured() ? 1 : null;
        String sortBy = request.getSortBy();

        List<Product> products = productMapper.search(keyword, categoryId, brand, minPrice, maxPrice,
                status, isFeatured, sortBy, offset, request.getPageSize());

        int total = productMapper.countSearch(keyword, categoryId, brand, minPrice, maxPrice, status, isFeatured);

        List<ProductDTO> dtos = products.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(new PageResult<>((long)total, dtos, (long)request.getPageNum(), (long)request.getPageSize()));
    }

    @Override
    public Result<PageResult<ProductDTO>> getProductsByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findByCategoryId(categoryId, offset, pageSize);
        int total = productMapper.countByCategoryId(categoryId);

        List<ProductDTO> dtos = products.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(new PageResult<>((long)total, dtos, (long)pageNum, (long)pageSize));
    }

    @Override
    public Result<PageResult<ProductDTO>> getFeaturedProducts(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.search(null, null, null, null, null, 1, 1, "created_at", offset, pageSize);
        int total = productMapper.countSearch(null, null, null, null, null, 1, 1);

        List<ProductDTO> dtos = products.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(new PageResult<>((long)total, dtos, (long)pageNum, (long)pageSize));
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        dto.setIsFeatured(product.getIsFeatured() == 1);

        // 获取产品图片
        dto.setImages(productImageMapper.findByProductId(product.getId()).stream().map(img -> {
            ProductImageDTO imgDto = new ProductImageDTO();
            BeanUtils.copyProperties(img, imgDto);
            imgDto.setIsPrimary(img.getIsPrimary() == 1);
            return imgDto;
        }).collect(Collectors.toList()));

        // 获取产品SKU
        dto.setSkus(productSkuMapper.findByProductId(product.getId()).stream().map(sku -> {
            ProductSkuDTO skuDto = new ProductSkuDTO();
            BeanUtils.copyProperties(sku, skuDto);
            return skuDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}