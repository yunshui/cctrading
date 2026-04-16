package com.cc.user.mapper;

import com.cc.user.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 地址Mapper
 */
@Mapper
public interface AddressMapper {

    /**
     * 根据ID查询地址
     */
    Address findById(@Param("id") Long id);

    /**
     * 查询用户的所有地址
     */
    List<Address> findByUserId(@Param("userId") Long userId);

    /**
     * 查询用户默认地址
     */
    Address findDefaultByUserId(@Param("userId") Long userId);

    /**
     * 插入地址
     */
    int insert(Address address);

    /**
     * 更新地址
     */
    int update(Address address);

    /**
     * 删除地址
     */
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 取消用户所有默认地址
     */
    int setDefault(@Param("userId") Long userId);

    /**
     * 设置指定地址为默认
     */
    int updateDefault(@Param("id") Long id, @Param("userId") Long userId);
}