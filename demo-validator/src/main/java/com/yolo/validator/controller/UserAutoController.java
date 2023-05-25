package com.yolo.validator.controller;

import cn.hutool.json.JSONUtil;
import com.yolo.validator.common.dto.ApiResponse;
import com.yolo.validator.common.validator.group.Update;
import com.yolo.validator.domain.Person;
import com.yolo.validator.domain.Phone;
import com.yolo.validator.domain.UserParam;
import com.yolo.validator.domain.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;

@RestController
@RequestMapping("/user/auto")
@Validated
public class UserAutoController {
    @GetMapping
    public ApiResponse testOne(@Max(value = 4,message = "最大值不能超过4") int id){
        return ApiResponse.ofSuccess(id);
    }

    @GetMapping("/testData")
    public ApiResponse testData(@Valid Phone phone){
        return ApiResponse.ofSuccess(JSONUtil.toJsonStr(phone));
    }

    @PostMapping("/insert2")
    public ApiResponse getUser2(@Valid @RequestBody UserParam userParam){
        return ApiResponse.ofSuccess(JSONUtil.toJsonStr(userParam));
    }

    @PostMapping("/update")
    public ApiResponse update(@Validated({Update.class}) UserVO userVO) {
        return ApiResponse.ofSuccess(userVO);
    }

    @PostMapping("/insert3")
    public ApiResponse insert(@Valid UserVO userVO) {
        return ApiResponse.ofSuccess(userVO);
    }


    @PostMapping("/insert4")
    public ApiResponse insert4(@RequestBody @Valid Person person) {
        return ApiResponse.ofSuccess(person);
    }

}
