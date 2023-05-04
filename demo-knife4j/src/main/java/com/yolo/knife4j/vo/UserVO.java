package com.yolo.knife4j.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "用户实体", description = "用户description")
public class UserVO {
 
    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("学生集合")
    private List<StudentVO> studentVOS;
}