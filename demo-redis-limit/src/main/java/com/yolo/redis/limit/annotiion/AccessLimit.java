package com.yolo.redis.limit.annotiion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在需要保证 接口防刷限流 的Controller的方法上使用此注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    int DEFAULT_REQUEST = 10;
    int DEFAULT_TIME = 60;

    int maxCount() default DEFAULT_REQUEST;// 最大访问次数

    int seconds() default DEFAULT_TIME;// 固定时间, 单位: s

}
