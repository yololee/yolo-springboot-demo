package com.yolo.validator.common.validator.annotation;


import com.yolo.validator.common.validator.handler.ListValueConstrainValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE})
@Constraint(validatedBy = ListValueConstrainValidator.class)
public @interface ListValue {

    /**
     * 是否必填 默认是必填的
     */
    boolean required() default true;

    /**
     * 验证失败的消息
     */
    String message() default "集合校验失败";

    /**
     * 分组的内容
     */
    Class<?>[] groups() default {};
    /**
     * 错误验证的级别
     */
    Class<? extends Payload>[] payload() default {};


    int[] values() default {};
}