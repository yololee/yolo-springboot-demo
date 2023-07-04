package com.yolo.demo.common.validator.annotation;


import com.yolo.demo.common.validator.handler.NumberCheckValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberCheckValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE})
public @interface NumberCheck {

    /**
     * 是否必填 默认是必填的
     */
    boolean required() default true;

    /**
     * 最小值
     */
    int min() default 0;

    /**
     * 最大值
     */
    int max() default 1000000;

    /**
     * 最大小数点位数
     */
    int maxScale() default 2;

    /**
     * 默认错误提示信息
     */
    String message() default "参数校验失败!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
