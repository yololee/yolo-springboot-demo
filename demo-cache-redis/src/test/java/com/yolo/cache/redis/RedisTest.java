package com.yolo.cache.redis;

import com.yolo.cache.redis.entity.User;
import com.yolo.cache.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


@Slf4j
public class RedisTest extends DemoCacheRedisApplicationTests{

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testRedis(){
        // 测试线程安全，程序结束查看redis中count的值是否为1000
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        IntStream.range(0, 1000).forEach(i -> executorService.execute(() -> redisUtil.incr("count",1)));

        redisUtil.set("k1", "v1");
        String k1 = (String) redisUtil.get("k1");
        log.debug("【k1】= {}", k1);

        // 以下演示整合，具体Redis命令可以参考官方文档
        String key = "yolo:user:1";
        redisUtil.set(key,new User(1L, "user1"));
        // 对应 String（字符串）
        User user = (User) redisUtil.get(key);
        log.debug("【user】= {}", user);
    }


}
