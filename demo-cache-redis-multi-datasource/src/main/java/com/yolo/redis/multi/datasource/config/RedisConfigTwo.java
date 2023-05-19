package com.yolo.redis.multi.datasource.config;

 
import org.springframework.beans.factory.annotation.Value;
import lombok.Data;
import org.springframework.context.annotation.Configuration;


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