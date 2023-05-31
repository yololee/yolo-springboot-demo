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
