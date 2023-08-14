package com.yolo.demo.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.yolo.demo.anno.ExcelDictFormat;
import com.yolo.demo.anno.ExcelEnumFormat;
import com.yolo.demo.easyexcel.convert.ExcelDictConvert;
import com.yolo.demo.easyexcel.convert.ExcelEnumConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ExcelIgnoreUnannotated
@AllArgsConstructor
@NoArgsConstructor
public class DropdownDemo {
    @ExcelProperty(value = "用户名", index = 0)
    private String nickName;

    /**
     * 用户类型
     * </p>
     * 使用ExcelEnumFormat注解需要进行下拉选的部分
     */
    @ExcelProperty(value = "用户类型", index = 1, converter = ExcelEnumConvert.class)
    @ExcelEnumFormat(enumClass = UserStatus.class, textField = "info")
    private String userStatus;

    /**
     * 性别
     * <p>
     * 使用ExcelDictFormat注解需要进行下拉选的部分
     */
    @ExcelProperty(value = {"用户性别"},index = 2,converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "男,女,未知")
    private String gender;
}
