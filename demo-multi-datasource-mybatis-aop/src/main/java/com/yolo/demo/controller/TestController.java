package com.yolo.demo.controller;

import com.yolo.demo.entity.User;
import com.yolo.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final UserService userService;


    @PostMapping
    public void save(@RequestBody User user){
        userService.saveOneTest(user);
    }
}
