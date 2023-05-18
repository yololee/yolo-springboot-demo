package com.yolo.jackson.controller;

import com.yolo.jackson.domain.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {

    @GetMapping("/jackson/type1/res")
    public Model res() {
        return Model.builder().id(1).age(12).name("zhangsan").createTime(new Date()).build();
    }

    @GetMapping("/jackson/type1/res2")
    public Model res2() {
        return Model.builder().id(1).age(12).name("zhangsan").createTime(new Date()).build();
    }

    @GetMapping("/jackson/type1/res3")
    public Model res3() {
        return Model.builder().age(12).name("zhangsan").createTime(new Date()).build();
    }


}
