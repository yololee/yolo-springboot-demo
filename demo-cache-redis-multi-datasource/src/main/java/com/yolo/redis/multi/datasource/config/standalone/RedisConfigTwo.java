package com.yolo.redis.multi.datasource.config.standalone;
 
import java.util.List;

import com.yolo.redis.multi.datasource.config.cluster.RedisConfigOne;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
 
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 映射redis的单实例配置配置
 * yml:
   #redis单实例。单实例比较简单，ip，端口，密码，连接池就ok
spring: 
  redis2:
    timeout: 6000
    database: 0 
    host: localhost #单实例redis用这个配置
    password: #单实例redis用这个配置
    port: 6379 #单实例redis用这个配置
    lettuce:
      pool: 
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）

 *
 */
@Data
//@Configuration
@ConfigurationProperties(prefix="spring.redis2.cluster")
@EnableConfigurationProperties(RedisConfigTwo.class)
public class RedisConfigTwo {
 
 
    @Value("${spring.redis2.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis2.port:6379}")
    private int port;
    @Value("${spring.redis2.password:redis123}")
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