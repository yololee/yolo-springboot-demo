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
