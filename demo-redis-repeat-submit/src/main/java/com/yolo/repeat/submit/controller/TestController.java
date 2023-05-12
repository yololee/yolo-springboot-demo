package com.yolo.repeat.submit.controller;


import com.yolo.repeat.submit.annotation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试控制器
 *
 * @author jujueaoye
 * @date 2023/05/12
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @RepeatSubmit(interval = 500000)
    @GetMapping("/saveParam")
    public String saveParam(String name){
        return "保存Param成功" + name;
    }

    @RepeatSubmit(interval = 500000)
    @PostMapping("/saveParam2")
    public String saveParam2(@RequestBody List<Integer> ids){
        return "保存Param成功" + ids;
    }

}
