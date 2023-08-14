package com.yolo.demo.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.yolo.demo.anno.ExcelDictFormat;
import com.yolo.demo.anno.ExcelEnumFormat;
import com.yolo.demo.easyexcel.convert.ExcelDictConvert;
import com.yolo.demo.easyexcel.convert.ExcelEnumConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 带有下拉选的Excel导出
 */
@Data
@ExcelIgnoreUnannotated
@AllArgsConstructor
@NoArgsConstructor
public class ExportDemoVo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
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

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 3)
    private String phoneNumber;

    /**
     * Email
     */
    @ExcelProperty(value = "Email", index = 4)
    private String email;

    /**
     * 省
     * <p>
     * 级联下拉，仅判断是否选了
     */
    @ExcelProperty(value = "省", index = 5)
    @ColumnWidth(30)
    private String province;

    /**
     * 数据库中的省ID
     * </p>
     * 处理完毕后再判断是否市正确的值
     */
    private Integer provinceId;

    /**
     * 市
     * <p>
     * 级联下拉
     */
    @ExcelProperty(value = "市", index = 6)
    @ColumnWidth(30)
    private String city;

    /**
     * 数据库中的市ID
     */
    private Integer cityId;

    /**
     * 县
     * <p>
     * 级联下拉
     */
    @ExcelProperty(value = "县", index = 7)
    @ColumnWidth(30)
    private String area;

    /**
     * 数据库中的县ID
     */
    private Integer areaId;
}
