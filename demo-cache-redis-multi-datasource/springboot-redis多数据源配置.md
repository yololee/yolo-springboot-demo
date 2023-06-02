# springboot-redis多数据源配置

## 一、前言

本文将基于以下环境实现Redis的多数据源配置

1. springboot(2.3.12.RELEASE)
2. redis(7.0.7)
3. jedis(3.6.3)

## 二、实现

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- lettuce pool 缓存连接池 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
```

### 2、application.yml

```yml
spring:
  redis:
    timeout: 6000
    database: 0
    host: localhost
    port: 6379 #单实例redis用这个配置
    password:
    lettuce:
      pool:
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
  #redis单实例。单实例比较简单，ip，端口，密码，连接池就ok
  redis2:
    timeout: 6000
    database: 1
    host: localhost #单实例redis用这个配置
    password: #单实例redis用这个配置
    port: 6379 #单实例redis用这个配置
    lettuce:
      pool:
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
```

或者配置为下面的，可以省略下面三、四步骤

```yaml
spring:
  redis:
    database: 0
    password: 123456 #redis密码
    sentinel:
      master: mymaster
      nodes: 116.211.105.107:26379, 116.211.105.112:26380, 116.211.105.117:26381
```

### 3、redis配置类

```java
@Data
@Configuration
public class RedisConfigOne {
 
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout:6000}")
    private int timeout;
    @Value("${spring.redis.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private int maxWait;
}

@Data
@Configuration
public class RedisConfigTwo {
 
 
    @Value("${spring.redis2.host}")
    private String host;
    @Value("${spring.redis2.port:6379}")
    private int port;
    @Value("${spring.redis2.password}")
    private String password;
    @Value("${spring.redis2.timeout:6000}")
    private int timeout;
    @Value("${spring.redis2.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis2.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis2.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis2.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis2.lettuce.pool.max-wait}")
    private long maxWait;
}
```



```java
package com.yolo.redis.multi.datasource.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类 （注意设置key和value的序列化方式，否则存到redis里的数据会乱码）
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedisConfig {

    @Autowired
    private RedisConfigOne redisConfigOne;

    @Autowired
    private RedisConfigTwo redisConfigTwo;


    @Bean
    public GenericObjectPoolConfig<Object> redisPool1() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigOne.getMaxIdle());
        config.setMaxIdle(redisConfigOne.getMaxIdle());
        config.setMaxTotal(redisConfigOne.getMaxActive());
        config.setMaxWaitMillis(redisConfigOne.getMaxWait());
        return config;
    }
    @Bean
    public RedisStandaloneConfiguration redisConfig1() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisConfigOne.getHost());
        redisConfig.setPort(redisConfigOne.getPort());
        redisConfig.setDatabase(redisConfigOne.getDatabase());
        redisConfig.setPassword(RedisPassword.of(redisConfigOne.getPassword()));
        return redisConfig;
    }

    @Bean("factory1")
    @Primary
    public LettuceConnectionFactory factory1(@Qualifier("redisPool1") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig1") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("redisTemplate1")
    @Primary
    public RedisTemplate<String, Object> redisTemplate1(@Qualifier("factory1")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        //设置序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }



    @Bean
    public GenericObjectPoolConfig<Object> redisPool2() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigTwo.getMaxIdle());
        config.setMaxIdle(redisConfigTwo.getMaxIdle());
        config.setMaxTotal(redisConfigTwo.getMaxActive());
        config.setMaxWaitMillis(redisConfigTwo.getMaxWait());
        return config;
    }
    @Bean
    public RedisStandaloneConfiguration redisConfig2() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisConfigTwo.getHost());
        redisConfig.setPort(redisConfigTwo.getPort());
        redisConfig.setDatabase(redisConfigTwo.getDatabase());
        redisConfig.setPassword(RedisPassword.of(redisConfigTwo.getPassword()));
        return redisConfig;
    }

    @Bean("factory2")
    public LettuceConnectionFactory factory2(@Qualifier("redisPool2") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig2") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("redisTemplate2")
    public RedisTemplate<String, Object> redisTemplate2(@Qualifier("factory2")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        //设置序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
```

## 四、测试

```java
@Slf4j
@RestController
@RequestMapping("/test")
public class IndexController {

    @Resource(name = "redisTemplate1")
    private RedisTemplate<String,Object> redisTemplate;

    @Resource(name = "redisTemplate2")
    private RedisTemplate<String,Object> redisTemplate2;

    @PostMapping("redis")
    public ApiResponse saveData(String key) {
        redisTemplate.opsForValue().set(key, "hello world - reids");
        return ApiResponse.ofSuccess();
    }

    @GetMapping("redis")
    public ApiResponse getData(String key) {
        String dataStr = (String) redisTemplate.opsForValue().get(key);
        log.info("{}", dataStr);
        return ApiResponse.ofSuccess(dataStr);
    }


    @PostMapping("redis2")
    public ApiResponse saveData2(String key) {
        redisTemplate2.opsForValue().set(key, "hello world - reids2");
        return ApiResponse.ofSuccess();
    }

    @GetMapping("redis2")
    public ApiResponse getData2(String key) {
        String dataStr = (String) redisTemplate2.opsForValue().get(key);
        log.info("{}", dataStr);
        return ApiResponse.ofSuccess(dataStr);
    }

}
```

### 1、用redisTemplate存取数据

**存数据**

请求地址：（POST）127.0.0.1:8080/test/redis?key=test

![image-20230519142041951](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230519142041951.png)

**取数据**

请求地址：（GET）127.0.0.1:8080/test/redis?key=test

![image-20230519142148762](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230519142148762.png)

### 2、用redisTemplate2存取数据

**存数据**

请求地址：（POST）127.0.0.1:8080/test/redis2?key=test111

![image-20230519152932781](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230519152932781.png)

