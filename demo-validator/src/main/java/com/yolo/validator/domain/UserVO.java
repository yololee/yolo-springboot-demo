package com.yolo.validator.domain;

import com.yolo.validator.common.validator.group.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserVO {
    @NotNull
    private int id;
    @NotBlank(message = "name 不能为空",groups = Update.class)
    private String name;
}
