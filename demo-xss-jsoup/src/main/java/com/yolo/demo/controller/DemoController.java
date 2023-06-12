package com.yolo.demo.controller;


import com.yolo.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class DemoController {


    @GetMapping("/save/test1")
    public String test1(@RequestParam(name = "name",defaultValue = "") String name){
        log.info("名称为{}",name);
        return name;
    }

    @PostMapping("/save/test2")
    public User test2(@RequestBody User user){
        log.info("用户为{}",user);
        return user;
    }
}
