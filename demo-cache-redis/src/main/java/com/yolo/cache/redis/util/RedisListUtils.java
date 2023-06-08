package com.yolo.cache.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class RedisListUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //========================添加==========================
    //左插入元素，返回list元素个数
    public Long leftPush(String key, Object var) {
        return redisTemplate.opsForList().leftPush(key, var);
    }

    public Long leftPushAll(String key, Object... vars) {
        return redisTemplate.opsForList().leftPushAll(key, vars);
    }

    public Long leftPushAll(String key, Collection<Object> vars) {
        return redisTemplate.opsForList().leftPushAll(key, vars);
    }

    public Long leftPushIfPresent(String key, Object var) {
        return redisTemplate.opsForList().leftPushIfPresent(key, var);
    }

    //在元素var1的左边插入var2元素，返回list元素个数；元素var1不存在，不插入并返回-1
    public Long leftPush(String key, Object var1, Object var2) {
        return redisTemplate.opsForList().leftPush(key, var1, var2);
    }

    //右插入元素，返回list元素个数
    public Long rightPush(String key, Object var) {
        return redisTemplate.opsForList().rightPush(key, var);
    }

    public Long rightPushAll(String key, Object... vars) {
        return redisTemplate.opsForList().rightPushAll(key, vars);
    }

    public Long rightPushAll(String key, Collection<Object> vars) {
        return redisTemplate.opsForList().rightPushAll(key, vars);
    }

    public Long rightPushIfPresent(String key, Object var) {
        return redisTemplate.opsForList().rightPushIfPresent(key, var);
    }

    //在元素var1的右边插入var2元素，返回list元素个数；元素var1不存在，不插入并返回-1
    public Long rightPush(String key, Object var1, Object var2) {
        return redisTemplate.opsForList().rightPush(key, var1, var2);
    }

    //========================获取==========================
    //获取index对应的元素
    public Object index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    //获取下标区间[start, end]的元素;[0,-1]获取所有元素
    public List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    //========================修改==========================
    //更新下标index对应的元素值
    public void set(String key, long index, Object var) {
        redisTemplate.opsForList().set(key, index, var);
    }

    //========================移除==========================
    //根据count删除var值的元素，返回删除个数
    //count=0,删除所有var值的元素
    //|count|>count(var),删除所有var值的元素
    //count>0,正序删除count个数量的var元素
    //count<0,反序删除count个数量的var元素
    public Long remove(String key, long count, Object var) {
        return redisTemplate.opsForList().remove(key, count, var);
    }

    //移除不在下标区间[start, end]内的元素
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    //左弹出一个元素
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    //右弹出一个元素
    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    //key1右弹出，key2左插入
    public Object rightPopAndLeftPush(String key1, String key2) {
        return redisTemplate.opsForList().rightPopAndLeftPush(key1, key2);
    }

}

