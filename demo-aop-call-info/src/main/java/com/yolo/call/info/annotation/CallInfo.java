package com.yolo.call.info.annotation;

import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallInfo {
    String url() default "";
}
