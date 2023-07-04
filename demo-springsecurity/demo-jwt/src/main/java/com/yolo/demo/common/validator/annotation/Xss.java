package com.yolo.demo.common.validator.annotation;



import com.yolo.demo.common.validator.handler.XssValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义xss校验注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {XssValidator.class})
public @interface Xss {

    String message() default "xss攻击检查：参数含有非法攻击字符，已禁止继续访问！！";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}