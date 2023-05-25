package com.yolo.validator.common.validator.annotation;


import com.yolo.validator.common.validator.handler.VerifyIntegerCollectionDataValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @ClassName VerifyCollectionDataValid
 * @Description 自定义注解校验 Integer 类型的集合数据是否合法
 * @Author hl
 * @Date 2022/11/8 10:25
 * @Version 1.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {VerifyIntegerCollectionDataValidator.class})// 标明由哪个类执行校验逻辑
public @interface VerifyIntegerCollectionDataValid {

    // 校验出错时默认返回的消息
    String message() default "字段值不正确";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int[] values() default {}; // 指定值

    /**
     * 同一个元素上指定多个该注解时使用
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        VerifyIntegerCollectionDataValid[] value();
    }
}

