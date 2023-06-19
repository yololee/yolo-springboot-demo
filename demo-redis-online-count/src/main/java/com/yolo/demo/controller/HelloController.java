package com.yolo.demo.controller;

import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.dto.LoginParam;
import com.yolo.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @Autowired
    private UserService userService;


    /**
     * 该接口需要登录后才能操作
     * @return
     */
    @RequestMapping("/user/list")
    public ApiResponse hello(){
        return userService.selectUserList();
    }


    /**
     * 登录
     * @param loginParam
     * @return
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginParam loginParam){
        return userService.login(loginParam);
    }


    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request){
        return userService.logout(request);
    }


    /**
     * 获取当前在线人数
     * 这个就相当于一个心跳检查机制
     * 前端每间隔一定时间就请求一下该接口达到在线人数
     * @return
     */
    @PostMapping("/online")
    public ApiResponse getOnLineCount(){
        return userService.getOnLineCount();
    }
}

