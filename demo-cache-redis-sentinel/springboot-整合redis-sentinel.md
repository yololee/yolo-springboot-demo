# springboot-整合redis-sentinel

## 一、部署redis哨兵集群

[docker swarm 部署redis哨兵集群](https://gitee.com/huanglei1111/docker-compose/blob/master/Linux/redis/redis6.0.8/docker-swarm-redis-sentinel/docker-compose-redis-sentinel.yml)

## 二、整合

### 1、pom.xml

```xml
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
```

### 2、application.yml

```yml
spring:
  redis:
    timeout: 6000
    database: 0
    host: 192.168.10.10 #主节点的master
    password: 123456 #redis密码
    port: 6379 #主节点的master端口
    lettuce:
      pool:
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
    sentinel:
      master: mymaster
      password: 123456
      nodes:
        - 192.168.10.10:26379
        - 192.168.10.11:26380
        - 192.168.10.11:26381
```

### 3、RedisConfig

```java
package com.yolo.redis.sentinel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix="spring.redis.sentinel")
@EnableConfigurationProperties(RedisConfig.class)
public class RedisConfig {
 
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
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
    private long maxWait;

    List<String> nodes;
    
    @Value("${spring.redis.sentinel.master:mymaster}")
    private String master;

    @Value("${spring.redis.sentinel.password}")
    private String sentinelPassword;

}
```

### 4、RedisSentinelConfig

```java
package com.yolo.redis.sentinel.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisSentinelConfig {

    @Autowired
    private RedisConfig redisConfig;

    //读取pool配置
    @Bean
    public GenericObjectPoolConfig<Object> redisPool() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfig.getMaxIdle());
        config.setMaxIdle(redisConfig.getMaxIdle());
        config.setMaxTotal(redisConfig.getMaxActive());
        config.setMaxWaitMillis(redisConfig.getMaxWait());
        return config;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.sentinel(redisConfig.getHost(), redisConfig.getPort());
        redisSentinelConfiguration.setMaster(this.redisConfig.getMaster());
        redisSentinelConfiguration.setPassword(RedisPassword.of(redisConfig.getSentinelPassword()));
        if(redisConfig.getNodes()!=null) {
            List<RedisNode> sentinelNode= new ArrayList<>();
            for(String sen : redisConfig.getNodes()) {
                String[] arr = sen.split(":");
                sentinelNode.add(new RedisNode(arr[0],Integer.parseInt(arr[1])));
            }
            redisSentinelConfiguration.setSentinels(sentinelNode);
        }
        return redisSentinelConfiguration;
    }
    @Bean("factory")
    public LettuceConnectionFactory factory(@Qualifier("redisPool") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisSentinelConfiguration") RedisSentinelConfiguration redisSentinelConfiguration) {//注意传入的对象名和类型RedisSentinelConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisSentinelConfiguration, clientConfiguration);
    }

    /**
     * 哨兵redis数据源
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("factory")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
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

