package com.yolo.redis.multi.datasource.config.cluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
public class RedisClusterConfig {
    @Autowired
    RedisConfigOne redisConfigOne;//集群的配置，读取yml文件

    /************集群的配置--start*******************/
    //读取pool配置
    @Bean
    public GenericObjectPoolConfig<Object> redisPool() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigOne.getMaxIdle());
        config.setMaxIdle(redisConfigOne.getMaxIdle());
        config.setMaxTotal(redisConfigOne.getMaxActive());
        config.setMaxWaitMillis(redisConfigOne.getMaxWait());
        return config;
    }
    @Bean
    public RedisClusterConfiguration redisConfig() {//集群配置类
        RedisClusterConfiguration redisConfig = new RedisClusterConfiguration(redisConfigOne.getNodes());
        redisConfig.setMaxRedirects(redisConfigOne.getMaxRedirects());
        redisConfig.setPassword(RedisPassword.of(redisConfigOne.getPassword()));
        return redisConfig;
    }
    @Bean("factory")
    @Primary
    public LettuceConnectionFactory factory(@Qualifier("redisPool") GenericObjectPoolConfig<Object> config,
                                            @Qualifier("redisConfig") RedisClusterConfiguration redisConfig) {//注意传入的对象名和类型RedisClusterConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    /**
     * 集群redis数据源
     *
     * @param connectionFactory
     * @return
     */
    @Bean("redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("factory")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        //设置序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
    /************集群的配置--end*******************/


}
