package com.yolo.knife4j.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "学生实体", description = "学生description")
public class StudentVO {

    @ApiModelProperty("学生地址")
    private String address;
    @ApiModelProperty("学生编号")
    private Integer code;
}
