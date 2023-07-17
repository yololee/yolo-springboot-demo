package com.yolo.demo.service;

import com.baomidou.lock.annotation.Lock4j;
import com.yolo.demo.domain.User;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    //默认获取锁超时3秒，30秒锁过期
    @Lock4j
    public void simple() {
        //do something
    }

    //完全配置，支持spel
    @Lock4j(keys = {"#user.id", "#user.name"}, expire = 60000, acquireTimeout = 1000)
    public User customMethod(User user) {
        return user;
    }

}