package com.yolo.redis.multi.datasource.modules.system.controller;


import com.yolo.redis.multi.datasource.modules.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 测试api
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class IndexController {

    @Resource(name = "redisTemplate1")
    private RedisTemplate<String,Object> redisTemplate;

    @Resource(name = "redisTemplate2")
    private RedisTemplate<String,Object> redisTemplate2;

    @PostMapping("redis")
    public ApiResponse saveData(String key) {
        redisTemplate.opsForValue().set(key, "hello world - reids");
        return ApiResponse.ofSuccess();
    }

    @GetMapping("redis")
    public ApiResponse getData(String key) {
        String dataStr = (String) redisTemplate.opsForValue().get(key);
        log.info("{}", dataStr);
        return ApiResponse.ofSuccess(dataStr);
    }


    @PostMapping("redis2")
    public ApiResponse saveData2(String key) {
        redisTemplate2.opsForValue().set(key, "hello world - reids2");
        return ApiResponse.ofSuccess();
    }

    @GetMapping("redis2")
    public ApiResponse getData2(String key) {
        String dataStr = (String) redisTemplate2.opsForValue().get(key);
        log.info("{}", dataStr);
        return ApiResponse.ofSuccess(dataStr);
    }

}
