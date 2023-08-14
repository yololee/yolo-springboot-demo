# springboot-整合easyexcel-写操作

## 一、pom文件

```xml
               <!--web 依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>5.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>3.3.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.poi</groupId>
                    <artifactId>poi-ooxml-schemas</artifactId>
                </exclusion>
            </exclusions>
        </dependency>


        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.18</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
```

## 二、写操作

### 简单的写

```java
@Data
@NoArgsConstructor
public class SysUserExportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "登录名称")
    private String userName;

    /**
     * 用户邮箱
     */
    @ExcelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String phonenumber;

    /**
     * 用户性别(0男 1女 2未知)
     */
    @ExcelProperty(value = "用户性别")
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = "帐号状态")
    private String status;

}
```

```java 
@GetMapping("/demo1")
public void demo1(HttpServletResponse response){
    ExcelUtil.exportExcel(getFalseData(),"简单导出", SysUserExportVo.class,response);
}

private List<SysUserExportVo> getFalseData() {
    SysUserExportVo vo1 = new SysUserExportVo();
    vo1.setUserName("admin");
    vo1.setEmail("123456@qq.com");
    vo1.setPhonenumber("13412345678");
    vo1.setSex("1");
    vo1.setStatus("0");

    SysUserExportVo vo2 = new SysUserExportVo();
    vo2.setUserName("admin");
    vo2.setEmail("123456@qq.com");
    vo2.setPhonenumber("13412345678");
    vo2.setSex("1");
    vo2.setStatus("0");

   return ListUtil.of(vo1, vo2);
}
```

![image-20230814112315333](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814112315333.png)

### 指定写入的列

```java
@Data
@NoArgsConstructor
public class SysUserExportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "登录名称",index = 0)
    private String userName;

    /**
     * 用户邮箱
     */
    @ExcelProperty(value = "用户邮箱",index = 1)
    private String email;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码",index = 2)
    private String phonenumber;

    /**
     * 用户性别(0男 1女 2未知)
     */
    @ExcelProperty(value = "用户性别",index = 4)
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = "帐号状态",index = 6)
    private String status;

}
```

![image-20230814112725878](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814112725878.png)

### 复杂头写入

```java 
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
    @ExcelProperty(value = {"用户信息","用户性别"},index = 4)
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = {"用户信息","帐号状态"},index = 6)
    private String status;

}
```

![image-20230814113058364](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814113058364.png)

### 自定义格式转换

#### 固定格式转换样式

自定义注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDictFormat {

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default StringUtils.SEPARATOR;

}
```

自定义转换类

```java
package com.yolo.demo.easyexcel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.yolo.demo.anno.ExcelDictFormat;
import com.yolo.demo.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 字典格式化转换处理
 */
@Slf4j
public class ExcelDictConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String label = cellData.getStringValue();
        String value  = ExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());;
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        }
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String value = Convert.toStr(object);
        String label = ExcelUtil.convertByExp(value, anno.readConverterExp(), anno.separator());
        return new WriteCellData<>(label);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}

```

使用方式

```java
    /**
     * 用户性别(0男 1女 2未知)
     */
    @ExcelProperty(value = {"用户信息","用户性别"},index = 4,converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = {"用户信息","帐号状态"},index = 6,converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    private String status;


    @GetMapping("/demo2")
    public void demo2(HttpServletResponse response){
        ExcelUtil.exportExcel(getFalseData(),"类型转换导出", SysUserExportVo.class,response);
    }
```

![image-20230814141855105](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814141855105.png)

#### 枚举转换样式

自定义注解

```java
/**
 * 枚举格式化
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelEnumFormat {

    /**
     * 字典枚举类型
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 字典枚举类中对应的code属性名称，默认为code
     */
    String codeField() default "code";

    /**
     * 字典枚举类中对应的text属性名称，默认为text
     */
    String textField() default "text";

}
```

自定义转换类

```java
package com.yolo.demo.easyexcel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.yolo.demo.anno.ExcelEnumFormat;
import com.yolo.demo.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举格式化转换处理
 */
@Slf4j
public class ExcelEnumConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        cellData.checkEmpty();
        // Excel中填入的是枚举中指定的描述
        Object textValue;
        switch (cellData.getType()) {
            case STRING:
            case DIRECT_STRING:
            case RICH_TEXT_STRING:
                textValue = cellData.getStringValue();
                break;
            case NUMBER:
                textValue = cellData.getNumberValue();
                break;
            case BOOLEAN:
                textValue = cellData.getBooleanValue();
                break;
            default:
                throw new IllegalArgumentException("单元格类型异常!");
        }
        // 如果是空值
        if (ObjectUtil.isNull(textValue)) {
            return null;
        }
        Map<Object, String> enumCodeToTextMap = beforeConvert(contentProperty);
        // 从Java输出至Excel是code转text
        // 因此从Excel转Java应该将text与code对调
        Map<Object, Object> enumTextToCodeMap = new HashMap<>();
        enumCodeToTextMap.forEach((key, value) -> enumTextToCodeMap.put(value, key));
        // 应该从text -> code中查找
        Object codeValue = enumTextToCodeMap.get(textValue);
        return Convert.convert(contentProperty.getField().getType(), codeValue);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        }
        Map<Object, String> enumValueMap = beforeConvert(contentProperty);
        String value = Convert.toStr(enumValueMap.get(object), "");
        return new WriteCellData<>(value);
    }

    private Map<Object, String> beforeConvert(ExcelContentProperty contentProperty) {
        ExcelEnumFormat anno = getAnnotation(contentProperty.getField());
        Map<Object, String> enumValueMap = new HashMap<>();
        Enum<?>[] enumConstants = anno.enumClass().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            Object codeValue = ReflectUtils.invokeGetter(enumConstant, anno.codeField());
            String textValue = ReflectUtils.invokeGetter(enumConstant, anno.textField());
            enumValueMap.put(codeValue, textValue);
        }
        return enumValueMap;
    }

    private ExcelEnumFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelEnumFormat.class);
    }
}

```

使用方式

```java
    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = {"用户信息","帐号状态"},index = 6,converter = ExcelEnumConvert.class)
    @ExcelEnumFormat(enumClass = UserStatus.class, textField = "info")
    private String status;
```

![image-20230814143005240](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814143005240.png)

### 列单元格合并(合并列相同项)

自定义注解

```java
package com.yolo.demo.anno;




import com.yolo.demo.easyexcel.CellMergeStrategy;

import java.lang.annotation.*;

/**
 * excel 列单元格合并(合并列相同项)
 * 需搭配 {@link CellMergeStrategy} 策略使用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

	/**
	 * col index
	 */
	int index() default -1;

}

```

自定义实现合并策略

```java
package com.yolo.demo.easyexcel;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.yolo.demo.anno.CellMerge;
import com.yolo.demo.utils.ReflectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列值重复合并策略
 */
@Slf4j
public class CellMergeStrategy extends AbstractMergeStrategy {

    private final List<CellRangeAddress> cellList;
    private final boolean hasTitle;
    private int rowIndex;

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.hasTitle = hasTitle;
        // 行合并开始下标
        this.rowIndex = hasTitle ? 1 : 0;
        this.cellList = handle(list, hasTitle);
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // judge the list is not null
        if (CollUtil.isNotEmpty(cellList)) {
            // the judge is necessary
            if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == 0) {
                for (CellRangeAddress item : cellList) {
                    sheet.addMergedRegion(item);
                }
            }
        }
    }

    @SneakyThrows
    private List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        List<CellRangeAddress> cellList = new ArrayList<>();
        if (CollUtil.isEmpty(list)) {
            return cellList;
        }
        Field[] fields = ReflectUtils.getFields(list.get(0).getClass(), field -> !"serialVersionUID".equals(field.getName()));

        // 有注解的字段
        List<Field> mergeFields = new ArrayList<>();
        List<Integer> mergeFieldsIndex = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(CellMerge.class)) {
                CellMerge cm = field.getAnnotation(CellMerge.class);
                mergeFields.add(field);
                mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());
                if (hasTitle) {
                    ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                    rowIndex = Math.max(rowIndex, property.value().length);
                }
            }
        }

        Map<Field, RepeatCell> map = new HashMap<>();
        // 生成两两合并单元格
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < mergeFields.size(); j++) {
                Field field = mergeFields.get(j);
                Object val = ReflectUtils.invokeGetter(list.get(i), field.getName());

                int colNum = mergeFieldsIndex.get(j);
                if (!map.containsKey(field)) {
                    map.put(field, new RepeatCell(val, i));
                } else {
                    RepeatCell repeatCell = map.get(field);
                    Object cellValue = repeatCell.getValue();
                    if (cellValue == null || "".equals(cellValue)) {
                        // 空值跳过不合并
                        continue;
                    }
                    if (!cellValue.equals(val)) {
                        if (i - repeatCell.getCurrent() > 1) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex - 1, colNum, colNum));
                        }
                        map.put(field, new RepeatCell(val, i));
                    } else if (i == list.size() - 1) {
                        if (i > repeatCell.getCurrent()) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex, colNum, colNum));
                        }
                    }
                }
            }
        }
        return cellList;
    }

    @Data
    @AllArgsConstructor
    static class RepeatCell {

        private Object value;

        private int current;

    }
}

```

使用方式

```java
    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = {"用户信息","帐号状态"},index = 6,converter = ExcelEnumConvert.class)
    @ExcelEnumFormat(enumClass = UserStatus.class, textField = "info")
    @CellMerge
    private String status;

    @GetMapping("/demo3")
    public void demo3(HttpServletResponse response){
        ExcelUtil.exportExcel(getFalseData(),"合并单元格", SysUserExportVo.class,true,response);
    }
```

![image-20230814144650858](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814144650858.png)

### 简单下拉框

#### 第一种：指定列，指定下拉选项

```java
    @GetMapping("/demo4")
    public void demo4(HttpServletResponse response){
        DropDownOptions downOptions = new DropDownOptions(2,ListUtil.of("男","女"));
        DropdownDemo dropdownDemo = new DropdownDemo();
        ExcelUtil.exportExcel(ListUtil.of(dropdownDemo),"下拉选项", DropdownDemo.class,false,response,ListUtil.of(downOptions));
    }

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
//    @ExcelEnumFormat(enumClass = UserStatus.class, textField = "info")
    private String userStatus;

    /**
     * 性别
     * <p>
     * 使用ExcelDictFormat注解需要进行下拉选的部分
     */
    @ExcelProperty(value = {"用户性别"},index = 2,converter = ExcelDictConvert.class)
//    @ExcelDictFormat(readConverterExp = "男,女,未知")
    private String gender;
}
```

![image-20230814155024816](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814155024816.png)

#### 第二种：使用注解

```java
    @GetMapping("/demo4")
    public void demo4(HttpServletResponse response){
        DropDownOptions downOptions = new DropDownOptions();
        DropdownDemo dropdownDemo = new DropdownDemo();
        ExcelUtil.exportExcel(ListUtil.of(dropdownDemo),"下拉选项", DropdownDemo.class,false,response,ListUtil.of(downOptions));
    }

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
```



![image-20230814155242388](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814155242388.png)

### 联级下拉框

实体类

```java
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
```

逻辑处理

```java
 @GetMapping("/demo5")
    public void demo5(HttpServletResponse response){
        exportWithOptions(response);
    }

    public void exportWithOptions(HttpServletResponse response) {
        // 创建表格数据，业务中一般通过数据库查询
        List<ExportDemoVo> excelDataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // 模拟数据库中的一条数据
            ExportDemoVo everyRowData = new ExportDemoVo();
            everyRowData.setNickName("用户-" + i);
            everyRowData.setUserStatus(UserStatus.OK.getCode());
            everyRowData.setGender("1");
            everyRowData.setPhoneNumber(String.format("175%08d", i));
            everyRowData.setEmail(String.format("175%08d", i) + "@163.com");
            excelDataList.add(everyRowData);
        }

        // 通过@ExcelIgnoreUnannotated配合@ExcelProperty合理显示需要的列
        // 通过创建ExcelOptions来指定下拉框
        // 使用ExcelOptions时建议指定列index，防止出现下拉列解析不对齐

        // 首先从数据库中查询下拉框内的可选项
        // 这里模拟查询结果
        List<DemoCityData> provinceList = getProvinceList();
        List<DemoCityData> cityList = getCityList(provinceList);
        List<DemoCityData> areaList = getAreaList(cityList);
        int provinceIndex = 5, cityIndex = 6, areaIndex = 7;

        DropDownOptions provinceToCity = DropDownOptions.buildLinkedOptions(
                provinceList,
                provinceIndex,
                cityList,
                cityIndex,
                DemoCityData::getId,
                DemoCityData::getPid,
                everyOptions -> DropDownOptions.createOptionValue(everyOptions.getName(), everyOptions.getId())
        );

        DropDownOptions cityToArea = DropDownOptions.buildLinkedOptions(
                cityList,
                cityIndex,
                areaList,
                areaIndex,
                DemoCityData::getId,
                DemoCityData::getPid,
                everyOptions -> DropDownOptions.createOptionValue(everyOptions.getName(), everyOptions.getId())
        );

        // 把所有的下拉框存储
        List<DropDownOptions> options = new ArrayList<>();
        options.add(provinceToCity);
        options.add(cityToArea);

        // 到此为止所有的下拉框可选项已全部配置完毕

        // 接下来需要将Excel中的展示数据转换为对应的下拉选
        List<ExportDemoVo> outList = StreamUtils.toList(excelDataList, everyRowData -> {
            // 只需要处理没有使用@ExcelDictFormat注解的下拉框
            // 一般来说，可以直接在数据库查询即查询出省市县信息，这里通过模拟操作赋值
            everyRowData.setProvince(buildOptions(provinceList, everyRowData.getProvinceId()));
            everyRowData.setCity(buildOptions(cityList, everyRowData.getCityId()));
            everyRowData.setArea(buildOptions(areaList, everyRowData.getAreaId()));
            return everyRowData;
        });

        ExcelUtil.exportExcel(outList, "下拉框示例", ExportDemoVo.class, response, options);
    }

    private String buildOptions(List<DemoCityData> cityDataList, Integer id) {
        Map<Integer, List<DemoCityData>> groupByIdMap =
                cityDataList.stream().collect(Collectors.groupingBy(DemoCityData::getId));
        if (groupByIdMap.containsKey(id)) {
            DemoCityData demoCityData = groupByIdMap.get(id).get(0);
            return DropDownOptions.createOptionValue(demoCityData.getName(), demoCityData.getId());
        } else {
            return StrUtil.EMPTY;
        }
    }

    /**
     * 模拟查询数据库操作
     *
     * @return /
     */
    private List<DemoCityData> getProvinceList() {
        List<DemoCityData> provinceList = new ArrayList<>();

        // 实际业务中一般采用数据库读取的形式，这里直接拼接创建
        provinceList.add(new DemoCityData(0, null, "安徽省"));
        provinceList.add(new DemoCityData(1, null, "江苏省"));

        return provinceList;
    }

    /**
     * 模拟查找数据库操作，需要连带查询出省的数据
     *
     * @param provinceList 模拟的父省数据
     * @return /
     */
    private List<DemoCityData> getCityList(List<DemoCityData> provinceList) {
        List<DemoCityData> cityList = new ArrayList<>();

        // 实际业务中一般采用数据库读取的形式，这里直接拼接创建
        cityList.add(new DemoCityData(0, 0, "合肥市"));
        cityList.add(new DemoCityData(1, 0, "芜湖市"));
        cityList.add(new DemoCityData(2, 1, "南京市"));
        cityList.add(new DemoCityData(3, 1, "无锡市"));
        cityList.add(new DemoCityData(4, 1, "徐州市"));

        selectParentData(provinceList, cityList);

        return cityList;
    }

    /**
     * 模拟查找数据库操作，需要连带查询出市的数据
     *
     * @param cityList 模拟的父市数据
     * @return /
     */
    private List<DemoCityData> getAreaList(List<DemoCityData> cityList) {
        List<DemoCityData> areaList = new ArrayList<>();

        // 实际业务中一般采用数据库读取的形式，这里直接拼接创建
        areaList.add(new DemoCityData(0, 0, "瑶海区"));
        areaList.add(new DemoCityData(1, 0, "庐江区"));
        areaList.add(new DemoCityData(2, 1, "南宁县"));
        areaList.add(new DemoCityData(3, 1, "镜湖区"));
        areaList.add(new DemoCityData(4, 2, "玄武区"));
        areaList.add(new DemoCityData(5, 2, "秦淮区"));
        areaList.add(new DemoCityData(6, 3, "宜兴市"));
        areaList.add(new DemoCityData(7, 3, "新吴区"));
        areaList.add(new DemoCityData(8, 4, "鼓楼区"));
        areaList.add(new DemoCityData(9, 4, "丰县"));

        selectParentData(cityList, areaList);

        return areaList;
    }

    /**
     * 模拟数据库的查询父数据操作
     *
     * @param parentList /
     * @param sonList    /
     */
    private void selectParentData(List<DemoCityData> parentList, List<DemoCityData> sonList) {
        Map<Integer, List<DemoCityData>> parentGroupByIdMap =
                parentList.stream().collect(Collectors.groupingBy(DemoCityData::getId));

        sonList.forEach(everySon -> {
            if (parentGroupByIdMap.containsKey(everySon.getPid())) {
                everySon.setPData(parentGroupByIdMap.get(everySon.getPid()).get(0));
            }
        });
    }
```

![image-20230814161247595](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814161247595.png)

### 模版导出

#### 单列表多数据

```java
    /**
     * 单列表多数据
     */
    @GetMapping("/demo6")
    public void exportTemplateOne(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        map.put("title", "单列表多数据");
        map.put("test1", "数据测试1");
        map.put("test2", "数据测试2");
        map.put("test3", "数据测试3");
        map.put("test4", "数据测试4");
        map.put("testTest", "666");
        List<TestObj> list = new ArrayList<>();
        list.add(new TestObj("单列表测试1", "列表测试1", "列表测试2", "列表测试3", "列表测试4"));
        list.add(new TestObj("单列表测试2", "列表测试5", "列表测试6", "列表测试7", "列表测试8"));
        list.add(new TestObj("单列表测试3", "列表测试9", "列表测试10", "列表测试11", "列表测试12"));
        ArrayList<Object> objects = CollUtil.newArrayList(map, list);
        ExcelUtil.exportTemplate(objects, "单列表.xlsx", "excel/单列表.xlsx", response);
    }

    @Data
    @AllArgsConstructor
    static class TestObj1 {
        private String test1;
        private String test2;
        private String test3;
    }
```

![image-20230814161800367](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814161800367.png)

#### 多列表多数据

```java
    /**
     * 多列表多数据
     */
    @GetMapping("/demo7")
    public void exportTemplateMuliti(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        map.put("title1", "标题1");
        map.put("title2", "标题2");
        map.put("title3", "标题3");
        map.put("title4", "标题4");
        map.put("author", "Lion Li");
        List<TestObj1> list1 = new ArrayList<>();
        list1.add(new TestObj1("list1测试1", "list1测试2", "list1测试3"));
        list1.add(new TestObj1("list1测试4", "list1测试5", "list1测试6"));
        list1.add(new TestObj1("list1测试7", "list1测试8", "list1测试9"));
        List<TestObj1> list2 = new ArrayList<>();
        list2.add(new TestObj1("list2测试1", "list2测试2", "list2测试3"));
        list2.add(new TestObj1("list2测试4", "list2测试5", "list2测试6"));
        List<TestObj1> list3 = new ArrayList<>();
        list3.add(new TestObj1("list3测试1", "list3测试2", "list3测试3"));
        List<TestObj1> list4 = new ArrayList<>();
        list4.add(new TestObj1("list4测试1", "list4测试2", "list4测试3"));
        list4.add(new TestObj1("list4测试4", "list4测试5", "list4测试6"));
        list4.add(new TestObj1("list4测试7", "list4测试8", "list4测试9"));
        list4.add(new TestObj1("list4测试10", "list4测试11", "list4测试12"));
        Map<String, Object> multiListMap = new HashMap<>();
        multiListMap.put("map", map);
        multiListMap.put("data1", list1);
        multiListMap.put("data2", list2);
        multiListMap.put("data3", list3);
        multiListMap.put("data4", list4);
        ExcelUtil.exportTemplateMultiList(multiListMap, "多列表.xlsx", "excel/多列表.xlsx", response);
    }

    @Data
    @AllArgsConstructor
    static class TestObj {
        private String name;
        private String list1;
        private String list2;
        private String list3;
        private String list4;
    }
```

![image-20230814162151408](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230814162151408.png)