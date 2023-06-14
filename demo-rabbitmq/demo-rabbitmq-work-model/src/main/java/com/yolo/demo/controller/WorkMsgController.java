package com.yolo.demo.controller;


import com.yolo.demo.common.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/work")
@Slf4j
public class WorkMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        IntStream.range(0,10).forEach(num -> {
            rabbitTemplate.convertAndSend(MqConstant.WORK_QUEUE, msgContent + num);
            log.info("[生产者] 发送消息: {}", msgContent + num);
        });
    }


    @RabbitListener(queues = MqConstant.WORK_QUEUE)
    public void listener1(String msg) throws InterruptedException {
        Thread.sleep(200);
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(queues = MqConstant.WORK_QUEUE)
    public void listener2(String msg) throws InterruptedException {
        Thread.sleep(1000);
        log.info("[消费者2] 接收消息: {}", msg);
    }

}
