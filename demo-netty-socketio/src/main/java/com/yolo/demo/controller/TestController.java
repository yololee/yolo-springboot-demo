package com.yolo.demo.controller;

import com.yolo.demo.handler.ChatMessageEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {


    private final ChatMessageEventHandler chatMessageEventHandler;


    @GetMapping("/test")
    public void test(){
        chatMessageEventHandler.sendNamespace("123123");
    }
}
