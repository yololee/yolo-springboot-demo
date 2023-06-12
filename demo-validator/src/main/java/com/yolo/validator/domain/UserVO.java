package com.yolo.validator.domain;

import com.yolo.validator.common.validator.annotation.EnumCheck;
import com.yolo.validator.common.validator.annotation.NumberCheck;
import com.yolo.validator.common.validator.annotation.TextFormat;
import com.yolo.validator.common.validator.group.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserVO {
//    @NotNull
    @NumberCheck(required = false, min = 1)
    private int id;

    @NumberCheck(required = false, min = 1,max = 50)
    private long userId;

    @NumberCheck(required = false, min = 1,max = 50,maxScale = 3)
    private double roleId;
//    @NotBlank(message = "name 不能为空",groups = Update.class)
    @TextFormat(endsWith = "213")
    private String name;

    /**
     * 类型（测试，正式）
     */
    @NotNull(message = "类型不能为空")
    @EnumCheck(message = "类型不合法", enumClass = DemandTypeEnum.class)
    public Integer type;
}
