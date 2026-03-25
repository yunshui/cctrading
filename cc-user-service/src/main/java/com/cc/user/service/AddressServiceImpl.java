package com.cc.user.service;

import com.cc.common.constant.ErrorCode;
import com.cc.common.dto.AddressDTO;
import com.cc.common.dto.Result;
import com.cc.common.exception.BusinessException;
import com.cc.user.mapper.AddressMapper;
import com.cc.user.model.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务实现
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Result<List<AddressDTO>> getUserAddresses(Long userId) {
        List<Address> addresses = addressMapper.findByUserId(userId);
        List<AddressDTO> dtos = addresses.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.success(dtos);
    }

    @Override
    @Transactional
    public Result<AddressDTO> createAddress(Long userId, AddressDTO addressDTO) {
        Address address = toEntity(addressDTO);
        address.setUserId(userId);

        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.setDefault(userId);
        }

        addressMapper.insert(address);
        return Result.success(toDTO(address));
    }

    @Override
    @Transactional
    public Result<AddressDTO> updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }

        // 更新字段
        if (addressDTO.getReceiverName() != null) {
            address.setReceiverName(addressDTO.getReceiverName());
        }
        if (addressDTO.getReceiverPhone() != null) {
            address.setReceiverPhone(addressDTO.getReceiverPhone());
        }
        if (addressDTO.getProvince() != null) {
            address.setProvince(addressDTO.getProvince());
        }
        if (addressDTO.getCity() != null) {
            address.setCity(addressDTO.getCity());
        }
        if (addressDTO.getDistrict() != null) {
            address.setDistrict(addressDTO.getDistrict());
        }
        if (addressDTO.getDetail() != null) {
            address.setDetail(addressDTO.getDetail());
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.setDefault(userId);
            address.setIsDefault(1);
        }

        addressMapper.update(address);
        return Result.success(toDTO(address));
    }

    @Override
    @Transactional
    public Result<Void> deleteAddress(Long userId, Long addressId) {
        int rows = addressMapper.delete(addressId, userId);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "地址不存在");
        }

        addressMapper.setDefault(userId);
        addressMapper.updateDefault(addressId, userId);
        return Result.success();
    }

    private AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }

    private Address toEntity(AddressDTO dto) {
        Address entity = new Address();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}