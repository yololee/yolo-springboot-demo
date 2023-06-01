//package com.yolo.demosatoken.config;
//
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//
//@Data
//@Configuration
//@ConfigurationProperties(prefix="spring.redis.sentinel")
//@EnableConfigurationProperties(RedisConfig.class)
//public class RedisConfig {
//
//    @Value("${spring.redis.host}")
//    private String host;
//    @Value("${spring.redis.port}")
//    private int port;
//    @Value("${spring.redis.password}")
//    private String password;
//    @Value("${spring.redis.timeout:6000}")
//    private int timeout;
//    @Value("${spring.redis.database:0}")
//    private int database;
//
//    //pool映射
//    @Value("${spring.redis.lettuce.pool.max-active}")
//    private int maxActive;
//    @Value("${spring.redis.lettuce.pool.max-idle}")
//    private int maxIdle;
//    @Value("${spring.redis.lettuce.pool.min-idle}")
//    private int minIdle;
//    @Value("${spring.redis.lettuce.pool.max-wait}")
//    private long maxWait;
//
//    List<String> nodes;
//
//    @Value("${spring.redis.sentinel.master:mymaster}")
//    private String master;
//
//    @Value("${spring.redis.sentinel.password}")
//    private String sentinelPassword;
//
//}