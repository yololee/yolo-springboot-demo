# springboot-自定义注解实现接口限流

## 一、介绍

接口限流防刷：

限制同一个用户在限定时间内，只能访问固定次数。

思路：每次点击之后，在缓存中生成一个计数器，第一次将这个计数器置1后存入缓存，并给其设定有效期。

每次点击后，取出这个值，计数器加一，如果超过限定次数，就抛出业务异常

## 二、实现

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-redis</artifactId>
            <version>1.4.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
```

### 2、application.yml

```yml
spring:
  redis:
    # Redis服务器地址
    host: 127.0.0.1
    # Redis服务器端口号
    port: 6379
    # 使用的数据库索引，默认是0
    database: 0
    # 连接超时时间
    timeout: 1800000
    # 设置密码
    #    password: "123456"
    lettuce:
      pool:
        # 最大阻塞等待时间，负数表示没有限制
        max-wait: -1
        # 连接池中的最大空闲连接
        max-idle: 5
        # 连接池中的最小空闲连接
        min-idle: 0
        # 连接池中最大连接数，负数表示没有限制
        max-active: 20
```

### 3、自定义注解

```java
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

```

### 4、拦截器具体实现逻辑

```java
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

```

### 5、拦截器配置

```java
package com.yolo.redis.limit.config;

import com.yolo.redis.limit.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    /**
     * 跨域
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 接口防刷限流拦截器
 		 registry.addInterceptor(accessLimitInterceptor).addPathPatterns("/**");
    }
}

```

## 三、测试

我这里配置的是60s内只能访问三次，超过三次，抛出异常

```java
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    @AccessLimit(maxCount = 3,seconds = 60)
    public ApiResponse test(){
        return ApiResponse.ofSuccess("访问成功");
    }
}
```

![image-20230518142810994](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518142810994.png)

![image-20230518142829732](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518142829732.png)

