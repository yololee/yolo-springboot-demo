# Springboot-自定义注解打印参数信息

## 一、项目依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.20</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
```

## 二、自定义注解和切面类

```java 
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallInfo {
    String url() default "";
}
```

```java
package com.yolo.call.aspect;

import com.yolo.call.annotation.CallInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;


@Slf4j
@Aspect
@Component
public class CallInfoAspect {

    @Pointcut("@annotation(com.yolo.call.annotation.CallInfo)")
    public void callInfoCut() {
    }

    @Before("callInfoCut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CallInfo annotation = method.getAnnotation(CallInfo.class);
        String url = annotation.url();
        Object[] args = joinPoint.getArgs();
        log.info("方法: {} , 参数: {}", url, args);
    }

    @AfterReturning(pointcut = "callInfoCut()", returning = "data")
    public void afterReturnAdvice(JoinPoint joinPoint, Object data) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CallInfo annotation = method.getAnnotation(CallInfo.class);
        String url = annotation.url();
        log.info(url + " 返回值: " + data);
    }

    @AfterThrowing(value = "callInfoCut()", throwing = "ex")
    public void afterThrowAdvice(Throwable ex) {
        String classPrefix = "tm.customer.exception";
        // 非自定义异常，打印具体异常信息
        if (!ex.toString().startsWith(classPrefix)) {
            log.error("目标方法中抛出的异常：", ex);
        }
    }
}
```

## 三、测试

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/info")
    @CallInfo(url = "/test/info")
    public ApiResponse test(String name){
        return ApiResponse.ofSuccess(name);
    }
}
```

![image-20230510114749538](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230510114749538.png)

> [Gitee项目地址（demo-call-info）](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-call-info)

