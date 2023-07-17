package com.yolo.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedissonConfigure {

    @Resource
    private RedisProperties redisProperties;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (redisProperties.getSentinel() != null && !CollectionUtils.isEmpty(redisProperties.getSentinel().getNodes())) {
            Set<String> sentinels = new HashSet<>();
            for (String url : redisProperties.getSentinel().getNodes()) {
                String sentinel = "redis://" + url;
                sentinels.add(sentinel);
            }
            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(sentinels.toArray(new String[sentinels.size() - 1]))
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .setReadMode(ReadMode.MASTER);
            serverConfig.setDatabase(0);
            return Redisson.create(config);
        } else {
            config.useSingleServer()
                    .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
            config.setCodec(new JsonJacksonCodec());
            return Redisson.create(config);

        }
    }

}