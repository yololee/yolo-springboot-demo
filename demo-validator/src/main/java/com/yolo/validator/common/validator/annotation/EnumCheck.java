package com.yolo.validator.common.validator.annotation;

import com.yolo.validator.common.validator.handler.EnumCheckValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumCheckValidator.class)
public @interface EnumCheck {
    /**
     * 是否必填 默认是必填的
     */
    boolean required() default true;

    /**
     * 验证失败的消息
     */
    String message() default "枚举验证失败";

    /**
     * 分组的内容
     */
    Class<?>[] groups() default {};

    /**
     * 错误验证的级别
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 枚举的Class
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 枚举中的验证方法
     */
    String enumMethod() default "isInclude";


}

