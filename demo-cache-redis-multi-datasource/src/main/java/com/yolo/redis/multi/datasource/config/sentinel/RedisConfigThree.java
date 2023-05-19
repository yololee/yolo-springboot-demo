package com.yolo.redis.multi.datasource.config.sentinel;
 
import java.util.List;

import com.yolo.redis.multi.datasource.config.cluster.RedisConfigOne;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
 
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 映射redis的哨兵配置配置
 * yml:
 
  #redis哨兵.一主2从192.168.10.1:6379,192.168.10.2:6379,192.168.10.3:6379。三哨兵192.168.10.1:26379,192.168.10.2:26379,192.168.10.3:26379
spring:  
  redis3:
    timeout: 6000
    database: 0 
    host: 192.168.10.1 #主节点的master
    password: ha@123 #redis密码
    port: 6379 #主节点的master端口
    lettuce:
      pool: 
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
    sentinel: 
      master: mymaster 
      nodes: 
        - 192.168.10.1:26379
        - 192.168.10.2:26379
        - 192.168.10.3:26379

 *
 */
@Data
//@Configuration
@ConfigurationProperties(prefix="spring.redis3.sentinel")
@EnableConfigurationProperties(RedisConfigThree.class)
public class RedisConfigThree {
 
    @Value("${spring.redis3.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis3.port:6379}")
    private int port;
    @Value("${spring.redis3.password:redis123}")
    private String password;
    @Value("${spring.redis3.timeout:6000}")
    private int timeout;
    @Value("${spring.redis3.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis3.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis3.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis3.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis3.lettuce.pool.max-wait}")
    private long maxWait;
    
    //setinel映射
    List<String> nodes;//@ConfigurationProperties(prefix="spring.redis3.sentinel")映射
    
    @Value("${spring.redis3.sentinel.master:mymaster}")
    private String master;

}