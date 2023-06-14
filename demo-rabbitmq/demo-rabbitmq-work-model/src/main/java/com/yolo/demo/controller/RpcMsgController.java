package com.yolo.demo.controller;

import com.yolo.demo.common.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc")
@Slf4j
public class RpcMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send(){
        String msgContent = "Hello World";
        log.info("[生产者] 发送消息: {}", msgContent);
        Object msgObj = this.rabbitTemplate.convertSendAndReceive(MqConstant.RPC_QUEUE, msgContent);
        log.info("[生产者] 接收回应：{}", msgObj);
    }


    @RabbitListener(queues = MqConstant.RPC_QUEUE)
    public String listener(String msg) {
        log.info("[消费者] 接收消息: {}", msg);
        return "消费者back";
    }
}
