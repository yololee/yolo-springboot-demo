package com.yolo.redis.multi.datasource.config.cluster;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
 
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 映射redis的集群配置
 * yml:
  #默认用集群配置，3主3从共6节点的集群
spring: 
  redis:
    timeout: 6000
    database: 0
    port: 6379 #单实例redis用这个配置
    password: Redis@123
    cluster: #集群用这个配置
      nodes:
        - 127.0.0.1:7011
        - 127.0.0.1:7012
        - 127.0.0.1:7013
        - 127.0.0.1:7014
        - 127.0.0.1:7015
        - 127.0.0.1:7016
      max-redirects: 2 #获取失败 最大重定向次数
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
@ConfigurationProperties(prefix="spring.redis.cluster")
@EnableConfigurationProperties(RedisConfigOne.class)
public class RedisConfigOne {
 
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.password:redis123}")
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
    
    //cluster映射
    List<String> nodes;//@ConfigurationProperties(prefix="spring.redis.cluster")映射
    
    @Value("${spring.redis.cluster.max-redirects:3}")
    private int maxRedirects;
}