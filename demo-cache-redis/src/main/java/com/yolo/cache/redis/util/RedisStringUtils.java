package com.yolo.cache.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisStringUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    //========================添加==========================
    //存储key=value键值对
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }
    //存储key=value键值对并设置过期时间
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    //key不存在才存储，存在不操作
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Boolean setIfAbsent(String key, Object value, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    public Boolean setIfAbsent(String key, Object value, Duration duration) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, duration);
    }

    //key存在才存储，不存在不操作
    public Boolean setIfPresent(String key, Object value) {
        return redisTemplate.opsForValue().setIfPresent(key, value);
    }

    public Boolean setIfPresent(String key, Object value, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfPresent(key, value, time, timeUnit);
    }

    public Boolean setIfPresent(String key, Object value, Duration duration) {
        return redisTemplate.opsForValue().setIfPresent(key, value, duration);
    }

    //设置新值并返回旧值
    public Object getAndSet(String key, Object newValue) {
        return redisTemplate.opsForValue().getAndSet(key, newValue);
    }

    //========================获取==========================
    //批量存储
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public Boolean multiSetIfAbsent(Map<String, Object> map) {
        return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    //根据key获取value
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    //========================修改==========================
    //value值自增(+1)
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    //value值自增(incValue)
    public Long increment(String key, long incValue) {
        return redisTemplate.opsForValue().increment(key, incValue);
    }

    public Double increment(String key, double incValue) {
        return redisTemplate.opsForValue().increment(key, incValue);
    }
}
