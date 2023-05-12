# springboot-自定义注解实现防止重复提交

## 一、项目结构

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
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.75</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
```

2、application.yml

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

![image-20230512161231525](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512161231525.png)

## 二、实现

### 1、自定义注解

```java
package com.yolo.repeat.submit.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止表单重复提交
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    int interval() default 5000;

    /**
     * 提示消息
     */
    String message() default "不允许重复提交，请稍后再试";
}

```

### 2、编写拦截器

```java
package com.yolo.repeat.submit.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yolo.repeat.submit.annotation.RepeatSubmit;
import com.yolo.repeat.submit.common.AjaxResult;
import com.yolo.repeat.submit.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 防止重复提交拦截器
 */
@Component
public  class RepeatSubmitInterceptor implements HandlerInterceptor {
    
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    /**
     * 防重提交 redis key
     */
    public final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request, annotation)) {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.renderString(response, JSONUtil.toJsonStr(ajaxResult));
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request    请求对象
     * @param annotation 防复注解
     * @return 结果
     */
    public  boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation){
        // 本次参数及系统时间
        String nowParams = JSONUtil.toJsonStr(request.getParameterMap());
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一标识（指定key + 消息头）
        String cacheRepeatKey = REPEAT_SUBMIT_KEY + url;

        String sessionObj = redisTemplate.opsForValue().get(cacheRepeatKey);

        if (sessionObj != null) {
            Map<String, Object> sessionMap = JSON.parseObject(sessionObj,Map.class);
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap,annotation.interval())) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(url, nowDataMap);
        redisTemplate.opsForValue().set(cacheRepeatKey,JSONUtil.toJsonStr(cacheMap),annotation.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < interval;
    }
}
```

### 3、配置拦截器

```java
package com.yolo.repeat.submit.config;

import com.yolo.repeat.submit.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;


    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }
}
```

### 4、测试

```java
/**
 * 测试控制器
 *
 * @author jujueaoye
 * @date 2023/05/12
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @RepeatSubmit(interval = 500000)
    @GetMapping("/saveParam")
    public String saveParam(String name){
        return "保存Param成功" + name;
    }

    @RepeatSubmit(interval = 500000)
    @PostMapping("/saveParam2")
    public String saveParam2(@RequestBody List<Integer> ids){
        return "保存Param成功" + ids;
    }

}
```

![image-20230512162314280](../../../../../Library/Application Support/typora-user-images/image-20230512162314280.png)

![image-20230512162341341](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512162341341.png)

