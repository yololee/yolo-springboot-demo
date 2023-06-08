package com.yolo.cache.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RedisHashUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //========================添加==========================
    //存储元素
    public void put(String k, String hk, Object hv) {
        redisTemplate.opsForHash().put(k, hk, hv);
    }

    public void putAll(String k, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(k, map);
    }

    //hk不存在才会插入元素
    public Boolean putIfAbsent(String k, String hk, Object hv) {
        return redisTemplate.opsForHash().putIfAbsent(k, hk, hv);
    }

    //========================获取==========================
    //获取元素
    public Object get(String k, String hk) {
        return redisTemplate.opsForHash().get(k, hk);
    }

    public List<Object> multiGet(String k, Collection<String> hk) {
        return redisTemplate.opsForHash().multiGet(k, Collections.singleton(hk));
    }

    public Set<Object> keys(String k) {
        return redisTemplate.opsForHash().keys(k);
    }

    public List<Object> values(String k) {
        return redisTemplate.opsForHash().values(k);
    }

    public Map<Object, Object> entries(String k) {
        return redisTemplate.opsForHash().entries(k);
    }

    public Long size(String k) {
        return redisTemplate.opsForHash().size(k);
    }

    //========================修改==========================
    public Long increment(String k, String hk, long inc) {
        return redisTemplate.opsForHash().increment(k, hk, inc);
    }

    public Double increment(String k, String hk, double inc) {
        return redisTemplate.opsForHash().increment(k, hk, inc);
    }

    //========================删除==========================
    public Long delete(String k, Object... hvs) {
        return redisTemplate.opsForHash().delete(k, hvs);
    }

    //========================判断==========================
    public Boolean hasKey(String k, Object hv) {
        return redisTemplate.opsForHash().hasKey(k, hv);
    }

}

