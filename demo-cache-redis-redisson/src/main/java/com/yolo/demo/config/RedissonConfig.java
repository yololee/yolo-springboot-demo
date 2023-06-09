package com.yolo.demo.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    /**
     * Redisson配置
     */
    @Bean
    public RedissonClient redissonClient() {
        //1、创建配置
        Config config = new Config();

        redisHost = redisHost.startsWith("redis://") ? redisHost : "redis://" + redisHost;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redisHost + ":" + redisPort);

        if (StrUtil.isNotBlank(redisPassword)) {
            serverConfig.setPassword(redisPassword);
        }

        return Redisson.create(config);
    }

}
