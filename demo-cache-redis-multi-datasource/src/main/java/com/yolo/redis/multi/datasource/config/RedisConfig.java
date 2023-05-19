package com.yolo.redis.multi.datasource.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类 （注意设置key和value的序列化方式，否则存到redis里的数据会乱码）
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedisConfig {

    @Autowired
    private RedisConfigOne redisConfigOne;

    @Autowired
    private RedisConfigTwo redisConfigTwo;


    @Bean
    public GenericObjectPoolConfig<Object> redisPool1() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigOne.getMaxIdle());
        config.setMaxIdle(redisConfigOne.getMaxIdle());
        config.setMaxTotal(redisConfigOne.getMaxActive());
        config.setMaxWaitMillis(redisConfigOne.getMaxWait());
        return config;
    }
    @Bean
    public RedisStandaloneConfiguration redisConfig1() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisConfigOne.getHost());
        redisConfig.setPort(redisConfigOne.getPort());
        redisConfig.setDatabase(redisConfigOne.getDatabase());
        redisConfig.setPassword(RedisPassword.of(redisConfigOne.getPassword()));
        return redisConfig;
    }

    @Bean("factory1")
    @Primary
    public LettuceConnectionFactory factory1(@Qualifier("redisPool1") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig1") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("redisTemplate1")
    @Primary
    public RedisTemplate<String, Object> redisTemplate1(@Qualifier("factory1")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
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



    @Bean
    public GenericObjectPoolConfig<Object> redisPool2() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigTwo.getMaxIdle());
        config.setMaxIdle(redisConfigTwo.getMaxIdle());
        config.setMaxTotal(redisConfigTwo.getMaxActive());
        config.setMaxWaitMillis(redisConfigTwo.getMaxWait());
        return config;
    }
    @Bean
    public RedisStandaloneConfiguration redisConfig2() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisConfigTwo.getHost());
        redisConfig.setPort(redisConfigTwo.getPort());
        redisConfig.setDatabase(redisConfigTwo.getDatabase());
        redisConfig.setPassword(RedisPassword.of(redisConfigTwo.getPassword()));
        return redisConfig;
    }

    @Bean("factory2")
    public LettuceConnectionFactory factory2(@Qualifier("redisPool2") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig2") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("redisTemplate2")
    public RedisTemplate<String, Object> redisTemplate2(@Qualifier("factory2")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
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
