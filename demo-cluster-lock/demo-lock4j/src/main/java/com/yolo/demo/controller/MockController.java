package com.yolo.demo.controller;

import com.baomidou.lock.annotation.Lock4j;
import jodd.util.ThreadUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MockController {
 
    @GetMapping("/lockMethod")
    @Lock4j(keys = {"#key"}, acquireTimeout = 1000, expire = 10000)
    public Map<String,String> lockMethod(@RequestParam String key) {
        ThreadUtil.sleep(5000);
        Map<String,String> map = new HashMap<>();
        map.put("key",key);
        return map;
    }
}