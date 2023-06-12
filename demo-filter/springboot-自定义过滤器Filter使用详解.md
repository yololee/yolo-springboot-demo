# springboot-自定义过滤器Filter使用详解

## 一、Filter原理

Java Servlet API中提供了Filter接口，编写Filter的实现类，从而实现自定义过滤器。Filter的请求流程为：

- 客户端发起请求
- 服务容器判断当前请求资源是否有过滤器，有则执行过滤器
- 过滤器过滤通过后请求到Servlet服务器
- 返回结果通过过滤器返回给请求方

Filter接口源码：

```java
package javax.servlet;

import java.io.IOException;

public interface Filter {
    
    /**
     * filter对象只会创建一次，init方法也只会执行一次。
     */
    default void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
    *  该方法会对请求进行拦截，用户需要在该方法中自定义对请求内容以及响应内容进行过滤的，调用该方法的入参 FilterChain对象的 doFilter 方法对请求放行执行后面的逻辑，若未调用 doFilter 方法则本次请求结束，并向客户端返回响应失败
    */
    void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

    /**
    * 此方法用于销毁过滤器，过滤器被创建以后只要项目一直运行，过滤器就会一直存在，在项目停止时，会调用该方法销毁过滤器
    */
    default void destroy() {
    }
}
```

## 二、SpringBoot中Filter的实现

### 1、@WebFilter注解方式

> @WebFilter源码如下：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {
    String description() default "";

    String displayName() default "";

    WebInitParam[] initParams() default {};

    String filterName() default "";

    String smallIcon() default "";

    String largeIcon() default "";

    String[] servletNames() default {};

    String[] value() default {};

    String[] urlPatterns() default {};

    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};

    boolean asyncSupported() default false;
}
```

> 参数解释：

- urlPatterns：自定义需要拦截的URL，可以使用正则匹配，若没指定该参数值，则默认拦截所有请求
- filterName：自定义过滤器的名称
- initParams：自定义过滤器初始化参数的数组，此参数可以通过自定义过滤器 init() 的入参FilterConfig对象的 getInitParameter() 方法获取；（由于过滤器没有直接排除自定义URL不拦截的设定，如果我们需要在自定义拦截的URL中排除部分不需要拦截的URL，可以通过将需要排除的URL放到initParams参数中再在doFilter方法中排除）

#### 自定义一个拦截所有路径、排除/test的过滤器

```java
package com.yolo.demo.filter;

import cn.hutool.core.util.StrUtil;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "testFilter", urlPatterns = "/*", 
        initParams = @WebInitParam(name = "noFilterUrl", value = "/test"))
public class TestFilter implements Filter {
    private List<String> noFilterUrls; 
    
    @Override
    public void init(FilterConfig filterConfig){
        // 从过滤器配置中获取initParams参数
        String noFilterUrl = filterConfig.getInitParameter("noFilterUrl");
        // 将排除的URL放入成员变量noFilterUrls中
        if (StrUtil.isNotBlank(noFilterUrl)) {
            noFilterUrls = new ArrayList<>(Arrays.asList(noFilterUrl.split(",")));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            // 若请求中包含noFilterUrls中的片段则直接跳过过滤器进入下一步请求中
            HttpServletRequest hsr = (HttpServletRequest) servletRequest;
            String s = hsr.getRequestURI();

            boolean b = noFilterUrls.stream().anyMatch(s::contains);
            if (!b) {
                //过滤请求响应逻辑
                System.out.println("执行testFilter111111过滤器具体逻辑");
//                servletRequest = new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
```

<font color='red'>在启动类上需要添加`@ServletComponentScan`注解才能使过滤器生效</font>

#### @WebFilter注解方式注意事项

如果实现多个FIlter功能的过滤器。使用@WebFilter注解的方式只能根据过滤器名的类名顺序执行，添加@Order注解是无效的，因为@WebFilter在容器加载时，不会使用@Order注解定义的顺序，而是默认直接使用类名排序。所以使用这种方式实现多个过滤器，且有顺序要求，则需要注意类名的定义

![image-20230612162842436](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230612162842436.png)

![image-20230612162907294](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230612162907294.png)

![image-20230612162919578](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230612162919578.png)

### 2、自定义配置类注入FilterRegistrationBean对象配置Filter

这种方式和上面哪种方式类似。其实就是将上面那种方式的配置改为创建一个配置类对象，同时也支持配置过滤器执行的先后顺序

```java
package com.yolo.demo.config;


import com.yolo.demo.filter.TestFilter;
import com.yolo.demo.filter.TestFilter2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FilterConfig {

    @Autowired
    private TestFilter testFilter;

    @Autowired
    private TestFilter2 testFilter2;

    @Bean
    public FilterRegistrationBean<?> testFilterRegistration() {
        FilterRegistrationBean<TestFilter> registration = new FilterRegistrationBean<>();
        // 将过滤器配置到FilterRegistrationBean对象中
        registration.setFilter(testFilter);
        // 给过滤器取名
        registration.setName("testFilter");
        // 设置过滤器优先级，该值越小越优先被执行
        registration.setOrder(3);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("noFilterUrl", "/test");
        // 设置initParams参数
        registration.setInitParameters(paramMap);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        // 设置urlPatterns参数
        registration.setUrlPatterns(urlPatterns);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<?> test2FilterRegistration() {
        FilterRegistrationBean<TestFilter2> registration = new FilterRegistrationBean<>();
        // 将过滤器配置到FilterRegistrationBean对象中
        registration.setFilter(testFilter2);
        // 给过滤器取名
        registration.setName("test2Filter");
        // 设置过滤器优先级，该值越小越优先被执行
        registration.setOrder(0);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("noFilterUrl", "/test");
        // 设置initParams参数
        registration.setInitParameters(paramMap);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        // 设置urlPatterns参数
        registration.setUrlPatterns(urlPatterns);
        return registration;
    }
}
```

![image-20230612164220873](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230612164220873.png)


> 注意：使用这种方式去掉注解@ServletComponentScan一样生效

