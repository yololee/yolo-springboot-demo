package com.yolo.validator.controller;

import com.yolo.validator.common.dto.ApiResponse;
import com.yolo.validator.domain.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forest")
public class ForestController {

    @GetMapping("/hello")
    public String test1(){
        return "hello forest";
    }


    @PostMapping("/user/list")
    public ApiResponse test(){
        UserVO userVO = new UserVO();
        userVO.setId(1);
        userVO.setUserId(2);
        userVO.setRoleId(2.54);
        userVO.setName("lisi");
        userVO.setType(0);
        return ApiResponse.ofSuccess(userVO);
    }


}
