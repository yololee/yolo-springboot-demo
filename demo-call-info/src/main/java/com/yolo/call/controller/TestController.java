package com.yolo.call.controller;

import com.yolo.call.annotation.CallInfo;
import com.yolo.call.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/info")
    @CallInfo(url = "/test/info")
    public ApiResponse test(String name){
        return ApiResponse.ofSuccess(name);
    }
}
