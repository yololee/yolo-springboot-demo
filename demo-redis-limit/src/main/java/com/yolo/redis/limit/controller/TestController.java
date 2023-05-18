package com.yolo.redis.limit.controller;

import com.yolo.redis.limit.annotiion.AccessLimit;
import com.yolo.redis.limit.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    @AccessLimit(maxCount = 3,seconds = 60)
    public ApiResponse test(){
        return ApiResponse.ofSuccess("访问成功");
    }
}
