package com.yolo.redis.multi.datasource.config;
 

import org.springframework.beans.factory.annotation.Value;
import lombok.Data;
import org.springframework.context.annotation.Configuration;


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