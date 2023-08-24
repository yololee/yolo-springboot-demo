# springboot-自定义注解实现动态多数据源

## 一、多数据源整合的方式

使用分包方式，不同的数据源配置不同的MapperScan和mapper文件

使用AOP切片方式，实现动态数据源切换

使用数据库代理中间件，如Mycat等

区别：

分包方式可以集合JTA(JAVA Transactional API)实现分布式事务，但是整个流程的实现相对来说比较复杂

AOP动态配置数据源方式缺点在于无法实现全局分布式事务，所以如果只是对接第三方数据源，不涉及到需要保证分布式事务的话，是可以作为一种选择

开发者不需要关注太多与业务无关的问题，把它们都交给数据库代理中间件去处理，大量的通用的数据聚合，事务，数据源切换都由中间件来处理，中间件的性能与处理能力将直接决定应用的读写性能，比较常见的有Mycat、TDDL等

## 二、项目准备

### pom文件

```xml
        <!--web模块依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring核心aop模块依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
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
            <version>1.2.16</version>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.3.1</version>
        </dependency>
        <!--lombok模块依赖-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.18</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```

### application.yml配置类

```yml
server:
  port: 9090
# 数据源配置
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.jdbc.Driver
    druid:
      # 主库数据源
      master:
        url: jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
        username: root
        password: root
      # 从库数据源
      slave:
        # 从数据源开关/默认关闭
        enabled: true
        url: jdbc:mysql://localhost:3306/test1?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
        username: root
        password: root
      # 初始连接数
      initialSize: 5
      # 最小连接池数量
      minIdle: 10
      # 最大连接池数量
      maxActive: 20
      # 配置获取连接等待超时的时间
      maxWait: 60000
      # 配置连接超时时间
      connectTimeout: 30000
      # 配置网络超时时间
      socketTimeout: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      timeBetweenEvictionRunsMillis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      minEvictableIdleTimeMillis: 300000
      # 配置一个连接在池中最大生存的时间，单位是毫秒
      maxEvictableIdleTimeMillis: 900000
      # 配置检测连接是否有效
      validationQuery: SELECT 1 FROM DUAL
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      webStatFilter:
        enabled: true
      statViewServlet:
        enabled: true
        # 设置白名单，不填则允许所有访问
        allow:
        url-pattern: /druid/*
        # 控制台管理用户名和密码
        login-username: yolo
        login-password: 123456
      filter:
        stat:
          enabled: true
          # 慢SQL记录
          log-slow-sql: true
          slow-sql-millis: 1000
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true

mybatis-plus:
  #mybatis配置文件
  #config-location: classpath:mybatis-config.xml
  # mapper映射位置
  mapper-locations: classpath:/mapper/**Mapper.xml
  #所有domain别名类所在包
  type-aliases-package: com.yolo.demo.entity
  # 启动时是否检查 MyBatis XML 文件的存在，默认不检查
  check-config-location: false
  configuration:
    # 更详细的日志输出 会有性能损耗 org.apache.ibatis.logging.stdout.StdOutImpl
    # 关闭日志记录 (可单纯使用 p6spy 分析) org.apache.ibatis.logging.nologging.NoLoggingImpl
    # 默认日志输出 org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    #类属性与表字段的驼峰映射，mybatiplus默认true开启，mybatis需要手动配置，且config-location和configuration不能同时出现
    map-underscore-to-camel-case: true
    # MyBatis 自动映射策略
    # NONE：不启用 PARTIAL：只对非嵌套 resultMap 自动映射 FULL：对所有 resultMap 自动映射
    auto-mapping-behavior: partial
    # MyBatis 自动映射时未知列或未知属性处理策
    # NONE：不做处理 WARNING：打印相关警告 FAILING：抛出异常和详细信息
    auto-mapping-unknown-column-behavior: none
  #全局配置
  global-config:
    # 是否打印 Logo banner
    banner: true
    #数据库配置
    db-config:
      #主键策略
      # AUTO 自增 NONE 空 INPUT 用户输入 ASSIGN_ID 雪花 ASSIGN_UUID 唯一 UUID
      id-type: ASSIGN_ID  # IdType默认的全局
      #表名前缀为tb_，表名为前缀拼接类名（小写）
      #      table-prefix: tb_
      logic-delete-field: removed # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 2 # 逻辑已删除值
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
      # 字段验证策略之 insert,在 insert 的时候的字段验证策略
      # IGNORED 忽略 NOT_NULL 非NULL NOT_EMPTY 非空 DEFAULT 默认 NEVER 不加入 SQL
      insert-strategy: not_null
      # 字段验证策略之 update,在 update 的时候的字段验证策略
      update-strategy: not_null
      # 字段验证策略之 select,在 select 的时候的字段验证策略既 wrapper 根据内部 entity 生成的 where 条件
      where-strategy: not_null

```

### 准备sql

```sql
CREATE TABLE `user` (
  `id` bigint(64) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `age` int(30) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 三、实战

### 切换动态数据源

```java
package com.yolo.demo.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换处理
 */
public class DynamicDataSourceContextHolder {
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setDataSourceType(String dsType) {
        log.info("切换到{}数据源", dsType);
        CONTEXT_HOLDER.set(dsType);
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }
}

```

```java
package com.yolo.demo.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }
}
```

### druid 配置多数据源

```java
package com.yolo.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.yolo.demo.common.DataSourceType;
import com.yolo.demo.datasource.DynamicDataSource;
import com.yolo.demo.utils.SpringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * druid 配置多数据源
 */
@Configuration
public class DruidConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 设置数据源
     *
     * @param targetDataSources 备选数据源集合
     * @param sourceName        数据源名称
     * @param beanName          bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName) {
        try {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        } catch (Exception ignored) {
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter() {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy() {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
```

### 自定义注解和切面实现类

```java
package com.yolo.demo.anno;

import com.yolo.demo.common.DataSourceType;

import java.lang.annotation.*;

/**
 * 自定义多数据源切换注解
 * 优先级：先方法，后类，如果方法覆盖了类上的数据源类型，以方法的为准，否则以类上的为准
 * @author jujueaoye
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    /**
     * 切换数据源名称
     */
    DataSourceType value() default DataSourceType.MASTER;
}

```

```java
package com.yolo.demo.common;

/**
 * 数据源
 */
public enum DataSourceType {
    /**
     * 主库
     */
    MASTER,

    /**
     * 从库
     */
    SLAVE
}

```

```java
package com.yolo.demo.aspect;

import java.util.Objects;

import cn.hutool.core.util.ObjectUtil;
import com.yolo.demo.anno.DataSource;
import com.yolo.demo.datasource.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 多数据源处理
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.yolo.demo.anno.DataSource)"
            + "|| @within(com.yolo.demo.anno.DataSource)")
    public void dsPointCut() {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);

        if (ObjectUtil.isNotNull(dataSource)) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        if (Objects.nonNull(dataSource)) {
            return dataSource;
        }
        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }
}
```

### 启动类排除自动加载数据库配置

```java
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class DemoMultiDatasourceMybatisAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMultiDatasourceMybatisAopApplication.class, args);
    }

}
```

## 四、测试

```java
    @DataSource(DataSourceType.SLAVE)
    @Override
    public void saveOneTest(User user) {
        save(user);
    }
```

从库添加一条数据

![image-20230824095037078](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230824095037078.png)

