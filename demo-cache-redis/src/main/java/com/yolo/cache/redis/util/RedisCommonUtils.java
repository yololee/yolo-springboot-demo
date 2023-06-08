package com.yolo.cache.redis.util;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCommonUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCommonUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //========================删除==========================
    //阻塞删除
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    //阻塞删除、批量
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    //非阻塞删除，另开线程处理，对于大型LIST或HASH的分配太多,它会长时间阻塞Redis，可以用该方法
    public Boolean unlink(String key) {
        return redisTemplate.unlink(key);
    }
    //非阻塞删除、批量
    public Long unlink(Collection<String> keys) {
        return redisTemplate.unlink(keys);
    }

    //========================修改==========================
    //设置key的过期时间
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.expire(key, time, timeUnit);
    }

    public Boolean expire(String key, Duration duration) {
        return redisTemplate.expire(key, duration);
    }

    //设置key在指定Date时间之后过期
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    //移除key的过期时间，使key永不过期
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    //重命名key
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    //newKey不存在才重命名，存在不操作
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    //========================判断==========================
    //判断key是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    //========================获取==========================
    //根据匹配规则获取key集合
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
    //获取过期时间
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
    //获取过期时间并转换成对应的时间单位
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }
    //获取数据类型
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    //随机获得一个key
    public String randomKey() {
        return redisTemplate.randomKey();
    }

}

