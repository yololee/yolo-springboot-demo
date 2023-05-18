package com.yolo.redis.limit.interceptor;



import cn.hutool.core.convert.Convert;
import com.yolo.redis.limit.annotiion.AccessLimit;
import com.yolo.redis.limit.exception.AccessLimitException;
import com.yolo.redis.limit.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 接口防刷限流拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    private static final String ACCESS_LIMIT_PREFIX = "accessLimit:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            //如果是HandlerMethod 类，强转，拿到注解
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AccessLimit annotation = method.getAnnotation(AccessLimit.class);
        if (annotation != null) {
            check(annotation, request);
        }

        return true;
    }

    private void check(AccessLimit annotation, HttpServletRequest request) {
        //获取方法上注解的参数
        int maxCount = annotation.maxCount();
        int seconds = annotation.seconds();



        String key = ACCESS_LIMIT_PREFIX + IpUtil.getIpAddr() + request.getRequestURI();

        Boolean exists = stringRedisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(exists)) {
            //如果没有，说明没访问过，置1
            stringRedisTemplate.opsForValue().set(key,String.valueOf(1),seconds, TimeUnit.SECONDS);
        } else {
            int count = Convert.toInt(stringRedisTemplate.opsForValue().get(key));
            if (count < maxCount) {
                //设置 如果小于我们的防刷次数
                int ttl =Convert.toInt( stringRedisTemplate.getExpire(key));
                if (ttl <= 0) {
                    stringRedisTemplate.opsForValue().set(key,String.valueOf(1),seconds, TimeUnit.SECONDS);
                } else {
                    //小于5 就+1
                    stringRedisTemplate.opsForValue().set(key,String.valueOf(++count),ttl, TimeUnit.SECONDS);
                }
            } else {//说明大于最大次数
                throw new AccessLimitException(500,"手速太快了，慢点儿吧");
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
