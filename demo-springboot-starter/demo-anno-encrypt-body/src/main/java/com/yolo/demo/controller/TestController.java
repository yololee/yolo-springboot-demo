package com.yolo.demo.controller;

import com.yolo.demo.annotation.DecryptRequest;
import com.yolo.demo.annotation.EncryptResponse;
import com.yolo.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
@Slf4j
@EncryptResponse
@DecryptRequest
public class TestController {


    @PostMapping
    public String jia(@RequestBody User user) {
        System.out.println(user);
        return "success";
    }
}
