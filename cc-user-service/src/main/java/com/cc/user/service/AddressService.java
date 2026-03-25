package com.cc.user.service;

import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取用户的所有地址
     */
    Result<List<AddressDTO>> getUserAddresses(Long userId);

    /**
     * 创建地址
     */
    Result<AddressDTO> createAddress(Long userId, AddressDTO addressDTO);

    /**
     * 更新地址
     */
    Result<AddressDTO> updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    /**
     * 删除地址
     */
    Result<Void> deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    Result<Void> setDefaultAddress(Long userId, Long addressId);
}