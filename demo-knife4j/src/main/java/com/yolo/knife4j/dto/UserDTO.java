package com.yolo.knife4j.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ApiModel("用户信息")
@Getter
@Setter
@ToString
public class UserDTO {
    @ApiModelProperty(value = "用户id")
    private Long id;
    @ApiModelProperty(value = "用户名",example = "李雷")
    private String username;
    @ApiModelProperty(value = "性别",example = "男")
    private String gender;
    @ApiModelProperty(value = "手机号码",example = "18888888888")
    private String phone;
    @ApiModelProperty(value = "用户收货地址信息")
    private UserAddressDTO userAddressDTO;
}