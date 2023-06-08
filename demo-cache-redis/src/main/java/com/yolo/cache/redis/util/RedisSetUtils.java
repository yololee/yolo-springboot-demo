package com.yolo.cache.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class RedisSetUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //========================添加==========================
    //添加元素，返回成功添加个数
    public Long add(String key, Object... vars) {
        return redisTemplate.opsForSet().add(key, vars);
    }

    //========================获取==========================
    //获取元素
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    //随机获取一个元素
    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    //随机获取count个数量的元素(元素会重复)
    public List<Object> randomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    //随机获取count个数量的元素(元素不重复)
    public Set<Object> distinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    //set集合弹出一个元素，集合元素个数减一
    public Object pop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public List<Object> pop(String key, long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }

    //集合大小
    public Long size(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    //========================移除==========================
    //移除元素，返回成功移除个数
    public Long remove(String key, Object... vars) {
        return redisTemplate.opsForSet().remove(key, vars);
    }

    //转移
    public Boolean move(String key1, Object var, String key2) {
        return redisTemplate.opsForSet().move(key1, var, key2);
    }

    //========================判断==========================
    //判断set集合是否有var元素
    public Boolean isMember(String key, Object var) {
        return redisTemplate.opsForSet().isMember(key, var);
    }

    //========================合集==========================
    //把所有集合的元素加在一起，然后去重
    public Set<Object> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    public Set<Object> union(String key1, Collection<String> keys) {
        return redisTemplate.opsForSet().union(key1, keys);
    }

    public Set<Object> union(Collection<String> keys) {
        return redisTemplate.opsForSet().union(keys);
    }

    public Long unionAndStore(String key1, String key2, String dest) {
        return redisTemplate.opsForSet().unionAndStore(key1, key2, dest);
    }

    public Long unionAndStore(String key1, Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().unionAndStore(key1, keys, dest);
    }

    public Long unionAndStore(Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().unionAndStore(keys, dest);
    }

    //========================交集==========================
    //所有集合都共有的元素
    public Set<Object> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    public Set<Object> intersect(String key1, Collection<String> keys) {
        return redisTemplate.opsForSet().intersect(key1, keys);
    }

    public Set<Object> intersect(Collection<String> keys) {
        return redisTemplate.opsForSet().intersect(keys);
    }

    public Long intersectAndStore(String key1, String key2, String dest) {
        return redisTemplate.opsForSet().intersectAndStore(key1, key2, dest);
    }

    public Long intersectAndStore(String key1, Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().intersectAndStore(key1, keys, dest);
    }

    public Long intersectAndStore(Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().intersectAndStore(keys, dest);
    }


    //========================差集==========================
    //以第一个集合为准，去除与其他集合共同的元素，最后只留下自身独有的元素
    public Set<Object> difference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    public Set<Object> difference(String key1, Collection<String> keys) {
        return redisTemplate.opsForSet().difference(key1, keys);
    }

    public Set<Object> difference(Collection<String> keys) {
        return redisTemplate.opsForSet().difference(keys);
    }

    public Long differenceAndStore(String key1, String key2, String dest) {
        return redisTemplate.opsForSet().differenceAndStore(key1, key2, dest);
    }

    public Long differenceAndStore(String key1, Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().differenceAndStore(key1, keys, dest);
    }

    public Long differenceAndStore(Collection<String> keys, String dest) {
        return redisTemplate.opsForSet().differenceAndStore(keys, dest);
    }

}
