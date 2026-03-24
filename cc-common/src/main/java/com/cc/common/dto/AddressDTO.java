package com.cc.common.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {

    private Long id;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    private Integer isDefault;

    private Long createdAt;
}