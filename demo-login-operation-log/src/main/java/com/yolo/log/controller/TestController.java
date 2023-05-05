package com.yolo.log.controller;

import com.yolo.log.annotation.Log;
import com.yolo.log.common.BusinessType;
import com.yolo.log.common.Constants;
import com.yolo.log.manager.AsyncManager;
import com.yolo.log.manager.factory.AsyncFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @GetMapping("/login")
    public String testLoginLog(){
        AsyncManager.me().execute(AsyncFactory.recordLoginInfo("zhangsan",Constants.LOGIN_SUCCESS,"OK"));
        return "OK";
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @GetMapping("/Oper")
    public String testOperLog(){
        return "OK";
    }
}
