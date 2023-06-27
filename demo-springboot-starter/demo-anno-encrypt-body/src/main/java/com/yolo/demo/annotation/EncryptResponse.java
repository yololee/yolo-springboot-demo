package com.yolo.demo.annotation;

import java.lang.annotation.*;



@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {
    /**
     * 是否对结果加密
     */
    boolean value() default true;
}
