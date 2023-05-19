package com.yolo.redis.multi.datasource.config.sentinel;

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

//@Configuration
public class RedisSentinelConfig {


    @Autowired
    RedisConfigThree redisConfigThree;//哨兵的配置，读取yml文件





    /************哨兵的配置--start*******************/
    //读取pool配置
    @Bean
    public GenericObjectPoolConfig<Object> redisPool3() {
        GenericObjectPoolConfig<Object> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(redisConfigThree.getMaxIdle());
        config.setMaxIdle(redisConfigThree.getMaxIdle());
        config.setMaxTotal(redisConfigThree.getMaxActive());
        config.setMaxWaitMillis(redisConfigThree.getMaxWait());
        return config;
    }
    @Bean
    public RedisSentinelConfiguration redisConfig3() {//哨兵的节点要写代码组装到配置对象中
        RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        redisConfig.sentinel(redisConfigThree.getHost(), redisConfigThree.getPort());
        redisConfig.setMaster(redisConfigThree.getMaster());
        redisConfig.setPassword(RedisPassword.of(redisConfigThree.getPassword()));
        if(redisConfigThree.getNodes()!=null) {
            List<RedisNode> sentinelNode=new ArrayList<RedisNode>();
            for(String sen : redisConfigThree.getNodes()) {
                String[] arr = sen.split(":");
                sentinelNode.add(new RedisNode(arr[0],Integer.parseInt(arr[1])));
            }
            redisConfig.setSentinels(sentinelNode);
        }
        return redisConfig;
    }
    @Bean("factory3")
    public LettuceConnectionFactory factory3(@Qualifier("redisPool3") GenericObjectPoolConfig<Object> config,
                                             @Qualifier("redisConfig3") RedisSentinelConfiguration redisConfig) {//注意传入的对象名和类型RedisSentinelConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    /**
     * 哨兵redis数据源
     *
     * @param connectionFactory
     * @return
     */
    @Bean("redisTemplate3")
    public RedisTemplate<String, Object> redisTemplate3(@Qualifier("factory3")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
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
    /************哨兵的配置--end*******************/
}
