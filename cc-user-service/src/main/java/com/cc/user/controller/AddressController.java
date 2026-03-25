package com.cc.user.controller;

import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import com.cc.user.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户的所有地址
     */
    @GetMapping
    public Result<List<AddressDTO>> getUserAddresses(@RequestHeader("X-User-Id") Long userId) {
        log.info("Get addresses for user: {}", userId);
        return addressService.getUserAddresses(userId);
    }

    /**
     * 创建地址
     */
    @PostMapping
    public Result<AddressDTO> createAddress(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddressDTO addressDTO) {
        log.info("Create address for user: {}", userId);
        return addressService.createAddress(userId, addressDTO);
    }

    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    public Result<AddressDTO> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressDTO addressDTO) {
        log.info("Update address {} for user: {}", addressId, userId);
        return addressService.updateAddress(userId, addressId, addressDTO);
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        log.info("Delete address {} for user: {}", addressId, userId);
        return addressService.deleteAddress(userId, addressId);
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId) {
        log.info("Set default address {} for user: {}", addressId, userId);
        return addressService.setDefaultAddress(userId, addressId);
    }
}