package com.example.demo.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;


/**
 * Shedlock配置类
 */
@Configuration
// 开启定时器
@EnableScheduling
// 开启定时任务锁，并设置默认锁最大时间为30分钟(PT为固定格式，M为时间单位-分钟)
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class ShedlockConfig {

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 使用redis存储
     */
    @Bean
    public LockProvider lockProvider(RedisTemplate<String,String> redisTemplate) {
        // keyPrefix: redis key的前缀
        // env和keyPrefix 主要用于区分数据来源，保证最终redis-key在使用时不串用即可  ex=> keyPrefix:dev:scheduledTaskName
        return new RedisLockProvider(Objects.requireNonNull(redisTemplate.getConnectionFactory()), env, "keyPrefix");
    }

}
