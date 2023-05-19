package com.yolo.redis.multi.datasource.config.standalone;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
public class RedisStandaloneConfig {

    @Autowired
    RedisConfigTwo redisConfigTwo;//单实例的配置，读取yml文件


    /************单实例的配置--start*******************/
    //读取pool配置
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
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisConfigTwo.getHost(),redisConfigTwo.getPort());
        redisConfig.setPassword(RedisPassword.of(redisConfigTwo.getPassword()));
        return redisConfig;
    }

    @Bean("factory2")
    public LettuceConnectionFactory factory2(@Qualifier("redisPool2") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig2") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    /**
     * 单实例redis数据源
     *
     * @param connectionFactory
     * @return
     */
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
    /************单实例的配置--end*******************/

}
