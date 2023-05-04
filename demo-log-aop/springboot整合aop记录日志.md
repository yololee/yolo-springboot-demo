@[TOC]
# Springboot：整合AOP记录日志

## 一、AOP概念

- **切面(Aspect)**

  ```java
  一个关注点的模块化，这个关注点可能会横切多个对象。事务管理是J2EE应用中一个关于横切关注点的很好的例子。在Spring AOP中，切面可以使用基于模式或者基于@Aspect注解的方式来实现
  ```

- **连接点(JoinPoint)**

  ```java
  在程序执行过程中某个特定的点，比如某方法调用的时候或者处理异常的时候。在SpringAOP中，一个连接点总是表示一个方法的执行
  ```

- **通知(Advice)**

  ```java
  在切面的某个特定的连接点上执行的动作。其中包括了Around、Before和After等不同类型的通知。许多AOP框架（包括Spring）都是以拦截器做通知模型，并维护一个以连接点为中心的拦截器链
  ```

- **切入点(PointCut)**

  ```java
  匹配连接点的断言。通知和一个切入点表达式关联，并在满足这个切入点的连接点上运行（例如，当执行某个特定名称的方法时），切入点表达式如何和连接点匹配是AOP的核心：Spring缺省使用AspectJ切入点语法
  ```

- **引入(Intorduction)**

  ```java
  用来给一个类型声明额外的方法或属性（也被称为连接类型声明）。Spring允许引入新的接口（以及一个对应的实现）到任何被代理的对象。例如，你可以使用引入来使一个bean实现接口，以便简化缓存机制
  ```

- **目标对象(Target Object)**

  ```java
  被一个或者多个切面所通知的对象。也被称做被通知对象。既然Spring AOP是通过运行时代理实现的，这个对象永远是一个被代理对象
  ```

- **AOP代理(Aop proxy)**

  ```java
  AOP框架创建的对象，用来实现切面契约（例如通知方法执行等等）。在Spring中，AOP代理可以是JDK动态代理或者CGLIB代理
  ```

- **植入(weaving)**

  ```java
  把切面连接到其它的应用程序类型或者对象上，并创建一个被通知的对象。这些可以在编译时（例如使用AspectJ编译器），类加载时和运行时完成。Spring和其他纯Java AOP框架一样，在运行时完成织入
  ```

## 二、切点表达式

### execution表达式

用于匹配方法执行的连接点，属于方法级别

**语法：**

execution(修饰符 返回值类型 方法名（参数）异常)

| 语法参数   | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| 修饰符     | 可选，如public，protected，写在返回值前，任意修饰符填`*`号就可以 |
| 返回值类型 | `必选`，可以使用`*`来代表任意返回值                          |
| 方法名     | `必选`，可以用`*`来代表任意方法                              |
| 参数       | `()`代表是没有参数，`(..)`代表是匹配任意数量，任意类型的参数，当然也可以指定类型的参数进行匹配，如要接受一个String类型的参数，则`(java.lang.String)`, 任意数量的String类型参数：`(java.lang.String..)` |
| 异常       | 可选，语法：`throws 异常`，异常是完整带包名，可以是多个，用逗号分隔 |

**符号：**

| 符号 | 描述                   |
| ---- | ---------------------- |
| *    | 匹配任意字符           |
| ..   | 匹配多个包或者多个参数 |
| +    | 表示类及其子类         |

**条件符：**

| 符号    | 描述 |
| ------- | ---- |
| &&、and | 与   |
| \|\|    | 或   |
| !       | 非   |

### 案例

**拦截com.gj.web包下的所有子包里的任意类的任意方法**

```java
execution(* com.gj.web..*.*(..))
```

**拦截com.gj.web.api.Test2Controller下的任意方法**

```java
execution(* com.gj.web.api.Test2Controller.*(..))
```

**拦截任何修饰符为public的方法**

```java
execution(public * * (..))
```

**拦截com.gj.web下的所有子包里的以ok开头的方法**

```java
execution(* com.gj.web..*.ok*(..))
```

## 三、AOP通知

在切面类中需要定义切面方法用于响应响应的目标方法，切面方法即为通知方法，通知方法需要用注解标识，AspectJ支持5种类型的通知注解

| 注解           | 描述                             |
| -------------- | -------------------------------- |
| @Before        | 前置通知, 在方法执行之前执行     |
| @After         | 后置通知, 在方法执行之后执行     |
| @AfterReturn   | 返回通知, 在方法返回结果之后执行 |
| @AfterThrowing | 异常通知, 在方法抛出异常之后     |
| @Around        | 环绕通知，围绕方法的执行         |

- @Before

  ```java
      @Before("testCut()")
      public void cutProcess(JoinPoint joinPoint) {
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          Method method = signature.getMethod();
          System.out.println("注解方式AOP开始拦截, 当前拦截的方法名: " + method.getName());
      }
  ```

- @After

  ```java
      @After("testCut()")
      public void after(JoinPoint joinPoint) {
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          Method method = signature.getMethod();
          System.out.println("注解方式AOP执行的方法 :"+method.getName()+" 执行完了");
      }
  ```

- @AfterReturn：**其中`value`表示切点方法，`returning`表示返回的结果放到result这个变量中**

  ```java
      /**
       * returning属性指定连接点方法返回的结果放置在result变量中
       * @param joinPoint 连接点
       * @param result 返回结果
       */
      @AfterReturning(value = "testCut()",returning = "result")
      public void afterReturn(JoinPoint joinPoint, Object result) {
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          Method method = signature.getMethod();
          System.out.println("注解方式AOP拦截的方法执行成功, 进入返回通知拦截, 方法名为: "+method.getName()+", 返回结果为: "+result.toString());
      }
  ```

- @AfterThrowing：**其中`value`表示切点方法，`throwing`表示异常放到e这个变量**

  ```java
      @AfterThrowing(value = "testCut()", throwing = "e")
      public void afterThrow(JoinPoint joinPoint, Exception e) {
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          Method method = signature.getMethod();
          System.out.println("注解方式AOP进入方法异常拦截, 方法名为: " + method.getName() + ", 异常信息为: " + e.getMessage());
      }
  ```

- @Around

  ```java
      @Around("testCut()")
      public Object testCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
          System.out.println("注解方式AOP拦截开始进入环绕通知.......");
              Object proceed = joinPoint.proceed();
              System.out.println("准备退出环绕......");
              return proceed;
      }
  ```

## 四、springboot中使用AOP

### 导出依赖

```xml
<dependency>
			<groupId>com.google.guava</groupId>
			<artifactId>guava</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

		<dependency>
			<groupId>cn.hutool</groupId>
			<artifactId>hutool-all</artifactId>
		</dependency>

		<!-- 解析 UserAgent 信息 -->
		<dependency>
			<groupId>eu.bitwalker</groupId>
			<artifactId>UserAgentUtils</artifactId>
		</dependency>
```

### 创建切面类

```java
package com.yolo.logaop.aspectj;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 使用 aop 切面记录请求日志信息
 * </p>
 *
 * @author yangkai.shen
 * @author chen qi
 * @date Created in 2018-10-01 22:05
 */
@Aspect
@Component
@Slf4j
public class AopLog {
    /**
     * 切入点
     */
    @Pointcut("execution(public * com.yolo.logaop.controller.*Controller.*(..))")
    public void log() {

    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {

        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        // 打印请求相关参数
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(header);

        final Log l = Log.builder()
            .threadId(Long.toString(Thread.currentThread().getId()))
            .threadName(Thread.currentThread().getName())
            .ip(getIp(request))
            .url(request.getRequestURL().toString())
            .classMethod(String.format("%s.%s", point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName()))
            .httpMethod(request.getMethod())
            .requestParams(getNameAndValue(point))
            .result(result)
            .timeCost(System.currentTimeMillis() - startTime)
            .userAgent(header)
            .browser(userAgent.getBrowser().toString())
            .os(userAgent.getOperatingSystem().toString()).build();

        log.info("Request Log Info : {}", JSONUtil.toJsonStr(l));

        return result;
    }

    /**
     *  获取方法参数名和参数值
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {

        final Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        final String[] names = methodSignature.getParameterNames();
        final Object[] args = joinPoint.getArgs();

        if (ArrayUtil.isEmpty(names) || ArrayUtil.isEmpty(args)) {
            return Collections.emptyMap();
        }
        if (names.length != args.length) {
            log.warn("{}方法参数名和参数值数量不一致", methodSignature.getName());
            return Collections.emptyMap();
        }
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], args[i]);
        }
        return map;
    }

    private static final String UNKNOWN = "unknown";

    /**
     * 获取ip地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String comma = ",";
        String localhost = "127.0.0.1";
        if (ip.contains(comma)) {
            ip = ip.split(",")[0];
        }
        if (localhost.equals(ip)) {
            // 获取本机真正的ip地址
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
        }
        return ip;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Log {
        // 线程id
        private String threadId;
        // 线程名称
        private String threadName;
        // ip
        private String ip;
        // url
        private String url;
        // http方法 GET POST PUT DELETE PATCH
        private String httpMethod;
        // 类方法
        private String classMethod;
        // 请求参数
        private Object requestParams;
        // 返回参数
        private Object result;
        // 接口耗时
        private Long timeCost;
        // 操作系统
        private String os;
        // 浏览器
        private String browser;
        // user-agent
        private String userAgent;
    }
}
```

### 自定义一个接口

```java
package com.yolo.logaop.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName TestController
 * @Description 测试aop
 * @Author hl
 * @Date 2023/5/4 14:29
 * @Version 1.0
 */
@Slf4j
@RestController
public class TestController {

    /**
     * 测试方法
     *   127.0.0.1:8080/demo/test?who=zhangsan
     * @param who 测试参数
     * @return {@link Dict}
     */
    @GetMapping("/test")
    public Dict test(String who) {
        return Dict.create().set("who", StrUtil.isBlank(who) ? "me" : who);
    }

    /**
     *  测试post json方法
     * @param map 请求的json参数
     * @return {@link Dict}
     */
    @PostMapping("/testJson")
    public Dict testJson(@RequestBody Map<String, Object> map) {

        final String jsonStr = JSONUtil.toJsonStr(map);
        log.info(jsonStr);
        return Dict.create().set("json", map);
    }
}
```

### 测试

![image-20230504145156925](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504145156925.png)