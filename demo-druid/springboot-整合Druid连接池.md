# springboot-整合Druid连接池

## 一、介绍

###  Druid是什么？

Druid是Java语言中最好的数据库连接池，能够提供强大的监控和扩展功能。

> 更多可参考官方文档：[https://github.com/alibaba/druid/](https://gitee.com/link?target=https%3A%2F%2Fgithub.com%2Falibaba%2Fdruid%2F)

## 二、springboot 整合 druid 入门

### 1、pom.xml

```xml
        <!--MySQL 5.1.47-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--druid 数据库连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.24</version>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>
```

### 2、application.yml

```yml
spring:
  # 配置数据源
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useSSL=false # MySQL在高版本需要指明是否进行SSL连接 解决则加上 &useSSL=false
    username: root
    password: root
    platform: mysql
    driver-class-name: com.mysql.jdbc.Driver
    #  ===================== ↓↓↓↓↓↓  使用druid数据源  ↓↓↓↓↓↓ =====================
    # 连接池类型，druid连接池springboot暂无法默认支持，需要自己配置bean
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initialSize: 5  # 连接池初始化连接数量
      minIdle: 5      # 连接池最小空闲数
      maxActive: 20   # 连接池最大活跃连接数
      # 配置获取连接等待超时的时间
      maxWait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      timeBetweenEvictionRunsMillis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      minEvictableIdleTimeMillis: 300000
      validationQuery: select 'x'
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      # 打开PSCache，并且指定每个连接上PSCache的大小
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      filters: stat,wall,slf4j
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      loginUsername: admin # SQL监控后台登录用户名
      loginPassword: admin  # SQL监控后台登录用户密码
```

### 3、DruidConfig.java

```java
package com.example.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Druid核心配置类
 * @description : Druid连接池监控平台 http://127.0.0.1:8080/druid/index.html
 */
@Configuration
public class DruidConfig {

    @Value("${spring.datasource.druid.loginUsername}")
    private String loginUsername;

    @Value("${spring.datasource.druid.loginPassword}")
    private String loginPassword;

    /**
     * 配置Druid监控
     *
     * @param :
     * @return: org.springframework.boot.web.servlet.ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        // 注册服务
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单(为空表示,所有的都可以访问,多个IP的时候用逗号隔开)
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP黑名单 (存在共同时，deny优先于allow)
        servletRegistrationBean.addInitParameter("deny", "127.0.0.2");
        // 设置控制台登录的用户名和密码
        servletRegistrationBean.addInitParameter("loginUsername", loginUsername);
        servletRegistrationBean.addInitParameter("loginPassword", loginPassword);
        // 是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 配置web监控的filter
     *
     * @param :
     * @return: org.springframework.boot.web.servlet.FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则
        Map<String, String> initParams = new HashMap<>(1);
        // 设置忽略请求
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.setInitParameters(initParams);
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "");
        filterRegistrationBean.addInitParameter("aopPatterns", "com.example.demo.service");
        // 验证所有请求
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    /**
     * 配置数据源 【 将所有前缀为spring.datasource下的配置项都加载到DataSource中 】
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    /**
     * 配置事物管理器
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
```

### 三、测试

访问 http://127.0.0.1:8080/druid/index.html查看监控信息

![image-20230519171532850](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230519171532850.png)