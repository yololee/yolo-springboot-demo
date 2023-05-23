package com.yolo.redis.limit.aop.aspect;

import cn.hutool.core.convert.Convert;
import com.yolo.redis.limit.aop.anno.Limit;
import com.yolo.redis.limit.aop.common.exception.AccessLimitException;
import com.yolo.redis.limit.aop.util.IpUtil;
import com.yolo.redis.limit.aop.util.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LimitAop {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ACCESS_LIMIT_PREFIX = "accessLimit:";

    @Around("@annotation(com.yolo.redis.limit.aop.anno.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿limit的注解
        Limit limit = method.getAnnotation(Limit.class);
        if (limit != null) {
            check(limit);
        }
        return joinPoint.proceed();
    }

    private void check(Limit limit) {
        //获取方法上注解的参数
        long timeout = limit.timeout();
        TimeUnit timeunit = limit.timeunit();
        //最多的访问限制次数
        double maxCount = limit.permitsPerSecond();
        String msg = limit.msg();

        HttpServletRequest request = ServletUtils.getRequest();
        String key = ACCESS_LIMIT_PREFIX + IpUtil.getIpAddr() + request.getRequestURI();

        Boolean exists = stringRedisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(exists)) {
            //如果没有，说明没访问过，置1
            stringRedisTemplate.opsForValue().set(key,String.valueOf(1),timeout, timeunit);
        } else {
            int count = Convert.toInt(stringRedisTemplate.opsForValue().get(key));
            if (count < maxCount) {
                //设置 如果小于我们的防刷次数
                int ttl =Convert.toInt( stringRedisTemplate.getExpire(key));
                if (ttl <= 0) {
                    stringRedisTemplate.opsForValue().set(key,String.valueOf(1),timeout, timeunit);
                } else {
                    //小于5 就+1
                    stringRedisTemplate.opsForValue().set(key,String.valueOf(++count),ttl, timeunit);
                }
            } else {//说明大于最大次数
                throw new AccessLimitException(500,msg);
            }
        }
    }
}
