package com.yolo.demo.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yolo.demo.anno.CellMerge;
import com.yolo.demo.anno.ExcelDictFormat;
import com.yolo.demo.anno.ExcelEnumFormat;
import com.yolo.demo.easyexcel.convert.ExcelDictConvert;
import com.yolo.demo.easyexcel.convert.ExcelEnumConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户对象导出VO
 */

@Data
@NoArgsConstructor
public class SysUserExportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @ExcelProperty(value = {"用户信息","登录名称"},index = 0)
    private String userName;

    /**
     * 用户邮箱
     */
    @ExcelProperty(value = {"用户信息","用户邮箱"},index = 1)
    private String email;

    /**
     * 手机号码
     */
    @ExcelProperty(value = {"用户信息","手机号码"},index = 2)
    private String phonenumber;

    /**
     * 用户性别(0男 1女 2未知)
     */
    @ExcelProperty(value = {"用户信息","用户性别"},index = 4,converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = {"用户信息","帐号状态"},index = 6,converter = ExcelEnumConvert.class)
    @ExcelEnumFormat(enumClass = UserStatus.class, textField = "info")
    @CellMerge
    private String status;

}
