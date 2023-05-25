# springboot-整合validator实现参数校验

## 一、介绍

### 1、简介

Bean Validation是Java定义的一套基于注解的数据校验规范，目前已经从JSR 303的1.0版本升级到JSR 349的1.1版本，再到JSR 380的2.0版本（2.0完成于2017.08），已经经历了三个版本 。需要注意的是，JSR只是一项标准，它规定了一些校验注解的规范，但没有实现，比如@Null、@NotNull、@Pattern等，它们位于 javax.validation.constraints这个包下。而hibernate validator是对这个规范的实现，并增加了一些其他校验注解，如 @NotBlank、@NotEmpty、@Length等，它们位于org.hibernate.validator.constraints这个包下

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

### 2、注解介绍

#### 内置注解

| 注解                       | 说明                                                     |
| -------------------------- | -------------------------------------------------------- |
| @Null                      | 被注释的元素必须为null                                   |
| @NotNull                   | 被注释的元素不能为null                                   |
| @AssertTrue                | 被注释的元素必须为true                                   |
| @AssertFalse               | 被注释的元素必须为false                                  |
| @Min(value)                | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| @Max(value)                | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| @DecimalMin(value)         | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| @DecimalMax(value)         | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| @Size(max,min)             | 被注释的元素的大小必须在指定的范围内                     |
| @Digits(integer, fraction) | 被注释的元素必须是一个数字，其值必须必须在可接受的范围内 |
| @Past                      | 被注释的元素必须是一个过去的日期                         |
| @Future                    | 被注释的元素必须是一个将来的日期                         |
| @Pattern(value)            | 被注释的元素必须符合指定的正则表达式                     |

#### 扩展注解

| 注解               | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| @NotBlank          | 被注释的元素不能为null，且长度必须大于0，只能用于注解字符串  |
| @Email             | 被注释的元素必须是电子邮箱地址                               |
| @Length(min=,max=) | 被注释的字符串的大小必须在指定的范围内                       |
| @NotEmpty          | 被注释的元素值不为null且不为空，支持字符串、集合、Map和数组类型 |
| @Range             | 被注释的元素必须在规定的范围内                               |

## 二、validator的使用（手动校验）

### 1、校验工具类

```java
package com.yolo.validator.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidatorUtil {
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    //返回map
    public static <T> Map<String,String> validate(T t, Class... groups){
        Validator validator=VALIDATOR_FACTORY.getValidator();
        Set validateResult=validator.validate(t,groups);
        //如果为空
        if (validateResult.isEmpty()){
            return Collections.emptyMap();
        }else{
            //不为空时表示有错误
            LinkedHashMap errors= Maps.newLinkedHashMap();
            //遍历
            Iterator iterator=validateResult.iterator();
            while (iterator.hasNext()){
                ConstraintViolation violation=(ConstraintViolation) iterator.next();
                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            return errors;
        }
    }
    //返回list
    public static Map<String,String> validateList(Collection<?> collection){
        //基础校验collection是否为空
        com.google.common.base.Preconditions.checkNotNull(collection);
        //遍历collection
        Iterator iterator=collection.iterator();
        Map errors;
        do {
            //如果循环下一个为空直接返回空
            if (!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object=iterator.next();
            errors=validate(object,new Class[0]);
        }while (errors.isEmpty());
        return errors;
    }

     // 校验某一对象是否合法
    public static Map<String,String> validateObject(Object first,Object... objects){
        if (objects !=null && objects.length > 0 ){
            return validateList(Lists.asList(first,objects));
        } else {
            return validate(first , new Class[0]);
        }
    }
    //校验参数方法
    public static void check(Object param) throws ParamException {
        Map<String,String> map= BeanValidatorUtil.validateObject(param);
        //如果错误集合map不为空则抛出异常
        if (MapUtils.isNotEmpty(map)){
            throw  new ParamException(ApiStatus.PARAM_ERROR.getCode(),map.toString());
        }
    }
}
```

### 2、校验一个对象

```java
@Data
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不可以为空")
    @Length(min = 1, max = 20, message = "用户名长度需要在20个字以内")
    private String username;

    @NotBlank(message = "电话不可以为空")
    @Pattern(regexp = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$",message = "只能是数字")
    @Length(min = 1, max = 13, message = "电话长度需要在13个字以内")
    private String telephone;

    @NotBlank(message = "邮箱不允许为空")
    @Pattern(regexp = "^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$",message = "邮箱格式不正确")
    @Length(min = 5, max = 50, message = "邮箱长度需要在50个字符以内")
    private String mail;

    @NotEmpty
    private List<Integer> lists;
  }
```

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/insert")
    public ApiResponse getUser2(@RequestBody UserParam userParam) {
        Map<String, String> map = BeanValidatorUtil.validateObject(userParam);
        if (MapUtils.isNotEmpty(map)) {
            throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),map.toString());
        }
        return ApiResponse.ofSuccess();
    }
}
```

![image-20230524142834447](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524142834447.png)

### 3、内嵌对象校验

```java
@Data
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不可以为空")
    @Length(min = 1, max = 20, message = "用户名长度需要在20个字以内")
    private String username;

    @NotBlank(message = "电话不可以为空")
    @Pattern(regexp = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$",message = "只能是数字")
    @Length(min = 1, max = 13, message = "电话长度需要在13个字以内")
    private String telephone;

    @NotBlank(message = "邮箱不允许为空")
    @Pattern(regexp = "^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$",message = "邮箱格式不正确")
    @Length(min = 5, max = 50, message = "邮箱长度需要在50个字符以内")
    private String mail;

    @NotEmpty
    private List<Integer> lists;

    @Valid
    private Phone phone;
  }

@Data
public class Phone {
	@NotBlank
  	private String operatorType;        
  	@NotBlank    
  	private String phoneNum;
}
```

![image-20230524143231375](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524143231375.png)

## 三、validator的使用(自动校验)

### 1、全局异常处理

```java
package com.yolo.validator.common.exception.core;

import com.yolo.validator.common.dto.ApiResponse;
import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse exceptionHandler(Exception e) {
        log.error("服务器出现未知错误", e);
        return ApiResponse.ofException(ApiStatus.UNKNOWN_ERROR);
    }


    /**
     * {@code @Valid}参数校验失败异常
     *  处理 json 请求体调用接口校验失败抛出的异常
     *  作用于 @Validated @Valid 注解，前端提交的方式为json格式有效，出现异常时会被该异常类处理。
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error("MethodArgumentNotValidException异常，参数校验异常：{}", msg);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,msg);
    }

    /**
     * {@code @Valid}参数校验失败异常
     * 处理 form data方式调用接口校验失败抛出的异常
     * 作用于 @Validated @Valid 注解，仅对于表单提交有效，对于以json格式提交将会失效
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(BindException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");

        log.error("BindException异常，参数校验异常：{}", msg);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,msg);
    }


    /**
     * {@code @Valid}参数校验失败异常
     * 作用于 @NotBlank @NotNull @NotEmpty 注解，校验单个String、Integer、Collection等参数异常处理
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.error("ConstraintViolationException，参数校验异常：{}", msg);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    /**
     * {@code @Valid}注解使用类型错误
     * 注解 @NotEmpty 用在集合类上面 （不能为null，且Size>0）
     * 注解 @NotBlank 用在String上面 （用于String,不能为null且trim()之后size>0）
     * 注解 @NotNull 用在基本类型上（不能为null，但可以为empty,没有Size的约束）
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(UnexpectedTypeException e) {
        log.error("UnexpectedTypeException，注解使用类型错误", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }
    


    /**
     * 无效的参数异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(IllegalArgumentException e) {
        log.error("无效的参数异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }


    /**
     * 参数绑定异常
     * 注解 @RequestParam 绑定参数错误
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException，参数绑定异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ParamException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

}
```

### 2、三种参数校验情况

> ==注：单个参数校验需要在参数上增加校验注解，并在类上标注`@Validated`==

```java
@RestController
@RequestMapping("/user/auto")
@Validated
public class UserAutoController {
    @GetMapping
    public ApiResponse testOne(@Max(value = 4,message = "最大值不能超过4") int id){
        return ApiResponse.ofSuccess(id);
    }

    @GetMapping("/testData")
    public ApiResponse testData(@Valid Phone phone){
        return ApiResponse.ofSuccess(JSONUtil.toJsonStr(phone));
    }

    @PostMapping("/insert2")
    public ApiResponse getUser2(@Valid @RequestBody UserParam userParam){
        return ApiResponse.ofSuccess(JSONUtil.toJsonStr(userParam));
    }
}
```

![image-20230524150612180](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524150612180.png)

![image-20230524150918865](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524150918865.png)

### 3、分组校验

如果同一个参数，需要在不同场景下应用不同的校验规则，就需要用到分组校验了。比如：新注册用户还没起名字，我们允许`name`字段为空，但是不允许将名字更新为空字符

> 步骤：
>
> 1. 定义一个分组类（或接口）
> 2. 在校验注解上添加`groups`属性指定分组
> 3. `Controller`方法的`@Validated`注解添加分组类

```java
package com.yolo.validator.common.validator.group;

import javax.validation.groups.Default;

public interface Update extends Default {
}

```

```java
@Data
public class UserVO {
    @NotNull
    private int id;
    @NotBlank(message = "name 不能为空",groups = Update.class)
    private String name;
}
```

```java
    @PostMapping("/update")
    public ApiResponse update(@Validated({Update.class}) UserVO userVO) {
        return ApiResponse.ofSuccess(userVO);
    }

    @PostMapping("/insert3")
    public ApiResponse insert(@Valid UserVO userVO) {
        return ApiResponse.ofSuccess(userVO);
    }
```

![image-20230524152009128](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524152009128.png)

> 注意：
>
> 校验注解(如： `@NotBlank`)和`@validated`默认都属于`Default.class`分组
>
> 在编写`Update`分组接口时，如果继承了Default，下面两个写法就是等效的：
>
>  @Validated({Update.class})
>  @Validated({Update.class,Default.class})

### 4、自定义注解进行校验

> 步骤：
>
> 1. 自定义校验注解
> 2. 编写校验者类

```java
package com.yolo.validator.common.validator.annotation;

import com.yolo.validator.common.validator.handler.TextFormatHandler;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义参数校验注解： @TextFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TextFormatHandler.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE})
public @interface TextFormat {

    /**
     * 判断字符串中是否包含中文 包含则抛出异常
     */
    boolean notChinese() default false;

    /**
     * 是否包含
     */
    int[] containsInt() default {};

    /**
     * 是否不包含
     */
    int[] notContainsInt() default {};

    /**
     * 非必填时候的校验
     */
    String notNeedFill() default "";

    /**
     * 是否包含,不包含抛出异常
     */
    String[] contains() default {};

    /**
     * 是否不包含,包含抛出异常
     */
    String[] notContains() default {};

    /**
     * 前缀以xx开始
     */
    String startWith() default "";

    /**
     * 后缀以xx结束
     */
    String endsWith() default "";

    /**
     * 默认错误提示信息
     */
    String message() default "参数校验失败!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

```

```java
package com.yolo.validator.common.validator.handler;


import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
import com.yolo.validator.common.validator.annotation.TextFormat;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数校验验证器
 */
public class TextFormatHandler implements ConstraintValidator<TextFormat, Object> {

    private boolean notChinese;
    private String[] contains;
    private String[] notContains;
    private int[] containsInt;
    private int[] notContainsInt;
    private String startWith;
    private String endsWith;
    private String notNeedFill;
    private String message;

    // 注解初始化时执行
    @Override
    public void initialize(TextFormat textFormat) {
        this.notChinese = textFormat.notChinese();
        this.contains = textFormat.contains();
        this.notContains = textFormat.notContains();
        this.startWith = textFormat.startWith();
        this.endsWith = textFormat.endsWith();
        this.message = textFormat.message();
        this.containsInt = textFormat.containsInt();
        this.notContainsInt = textFormat.notContainsInt();
        this.notNeedFill = textFormat.notNeedFill();
    }

    @Override
    public boolean isValid(Object type, ConstraintValidatorContext context) {
        boolean flag = true;

        if (type instanceof String) {
            String target = (String) type;
            checkNoeChinese(target);
            checkContainStr(target);
            checkNotContainStr(target);
            checkStartWith(target);
            checkEndsWith(target);
            checkNotNeedFill(target);
        } else if (type instanceof Integer) {
            int target = (Integer) type;
            checkContainInt(target);
            checkNotContainInt(target);
        }
        return flag;
    }

    private void checkNotNeedFill(String target) {
        if (!StringUtils.isEmpty(notNeedFill)) {
            switch (notNeedFill) {
                case "description":
                    //检查描述长度
                    if (target.length() >= 2){
                        throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
                    }
                case "place":
                    if (target.length() >= 50){
                        throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
                    }
                default:
            }
        }
    }

    private void checkNotContainInt(int target) {
        for (int notContainInt : notContainsInt) {
            if (target == notContainInt) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkContainInt(int target) {
        for (int containInt : containsInt) {
            if (target != containInt) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkEndsWith(String target) {
        if (!StringUtils.isEmpty(target)) {
            if (!target.endsWith(endsWith)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

    private void checkStartWith(String target) {
        if (!StringUtils.isEmpty(startWith)) {
            if (!target.startsWith(startWith)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

    private void checkNotContainStr(String target) {
        for (String notContain : notContains) {
            if (notContain.equals(target)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkContainStr(String target) {
        for (String contain : contains) {
            if (!contain.equals(target)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkNoeChinese(String target) {
        if (notChinese) {
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(target);
            boolean b = matcher.find();
            if (b) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

}

```

==自定义注解抛出异常和返回flase需要不同的处理方式==

**抛出异常**

当抛出特定的异常时，全局异常处理器捕获的是`ValidationException.class`异常

```java
    /**
     *{@code @Valid}处理自定义注解抛出异常
     */
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ValidationException e){
        log.error("ValidationException，注解使用类型错误", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getCause().getMessage());
    }
```

**返回false**

全局异常处理器捕获的是MethodArgumentNotValidException异常

## 四、@Validator和@Valid的区别

在检验 Controller 的入参是否符合规范时，使用 @Validated 或者 @Valid 在基本验证功能上没有太多区别。但是在分组、注解地方、嵌套验证等功能上两个有所不同：

1. 分组：

   @Validated：提供了一个分组功能，可以在入参验证时，根据不同的分组采用不同的验证机制

2. 注解使用地方：

   @Validated：可以用在类型、方法和方法参数上。但是不能用在成员属性（字段）上

   @Valid：可以用在方法、构造函数、方法参数和成员属性（字段）上

   两者是否能用于成员属性（字段）上直接影响能否提供嵌套验证的功能。

## 五、hibernate的校验模式

### 1、普通模式（默认是这个模式）

普通模式(会校验完所有的属性，然后返回所有的验证失败信息)

### 2、快速失败返回模式

快速失败返回模式(只要有一个验证失败，则返回)

failFast：true 快速失败返回模式 false 普通模式

```java
ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
        .configure()
        .failFast( true )
        .buildValidatorFactory();
Validator validator = validatorFactory.getValidator();
```

和 （hibernate.validator.fail_fast：true 快速失败返回模式 false 普通模式）

```java
ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
        .configure()
        .addProperty( "hibernate.validator.fail_fast", "true" )
        .buildValidatorFactory();
Validator validator = validatorFactory.getValidator();
```

### 3.配置hibernate Validator为快速失败返回模式

```java
@Configuration
public class ValidatorConfiguration {
    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure()
                .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }
}
```