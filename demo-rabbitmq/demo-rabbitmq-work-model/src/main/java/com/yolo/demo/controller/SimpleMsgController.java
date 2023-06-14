package com.yolo.demo.controller;

import com.yolo.demo.common.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simple")
@Slf4j
public class SimpleMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        log.info("[生产者] 发送消息: {}", msgContent);
        rabbitTemplate.convertAndSend(MqConstant.SIMPLE_QUEUE, msgContent);
    }


    @RabbitListener(queues = MqConstant.SIMPLE_QUEUE)
    public void listener(String msg) {
        log.info("[消费者] 接收消息: {}", msg);
    }

}
