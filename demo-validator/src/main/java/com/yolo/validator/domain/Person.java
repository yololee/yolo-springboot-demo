package com.yolo.validator.domain;

import com.yolo.validator.common.validator.annotation.TextFormat;
import com.yolo.validator.common.validator.annotation.VerifyIntegerCollectionDataValid;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class Person {


//    /**
//     * 用户名
//     */
//    @NotBlank
//    @TextFormat(notChinese = true,message = "用户名不能含有汉字")
//    private String username;
//
//    /**
//     * 真实姓名
//     */
//    @NotBlank
//    @TextFormat(startWith = "张",endsWith = "三")
//    private String realName;

//    @TextFormat(notNeedFill = "description",startWith = "张",endsWith = "三")
//    private String type;

    /**
     * 告警级别（默认0提示,1次要,2重要,3紧急）
     */
    @NotEmpty(message = "告警级别集合不能为空")
//    @VerifyIntegerCollectionDataValid(message = "告警级别不合法",values = {0,1,2,3})
    @TextFormat(containsInt = {0,1,2},message = "告警级别不合法")
    private List<Integer> levelList;

}
