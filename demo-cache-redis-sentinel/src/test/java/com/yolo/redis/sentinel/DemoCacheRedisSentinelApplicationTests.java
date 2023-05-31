package com.yolo.redis.sentinel;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@SpringBootTest(classes = DemoCacheRedisSentinelApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DemoCacheRedisSentinelApplicationTests {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void contextLoads() {
//        redisTemplate.opsForValue().set("address","武汉");
        String name = redisTemplate.opsForValue().get("name").toString();
        log.info(name);

    }

}
