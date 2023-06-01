package com.yolo.demosatoken.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.yolo.demosatoken.api.dto.AddUserDTO;
import com.yolo.demosatoken.api.dto.LoginDTO;
import com.yolo.demosatoken.api.service.UserService;
import com.yolo.demosatoken.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public ApiResponse login(@RequestBody @Validated LoginDTO loginDTO){
        return userService.login(loginDTO);
    }


    @PutMapping(value = "/user/add")
    @SaCheckLogin
    @SaCheckRole("admin")
    public ApiResponse add(@RequestBody @Validated AddUserDTO addUserDTO) {
        return userService.addUser(addUserDTO);
    }


    @GetMapping("/user/logout")
    @SaCheckLogin
    public ApiResponse logout(){
        StpUtil.logout();
        return ApiResponse.ofSuccess();
    }
}
