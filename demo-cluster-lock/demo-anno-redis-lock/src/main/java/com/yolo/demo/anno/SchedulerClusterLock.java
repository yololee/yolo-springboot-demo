package com.yolo.demo.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 分布式定时任务方法锁
 *
 * @author jujueaoye
 * @date 2023/07/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SchedulerClusterLock {
}
