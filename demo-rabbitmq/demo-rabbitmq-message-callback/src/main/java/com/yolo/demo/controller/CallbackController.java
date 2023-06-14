package com.yolo.demo.controller;

import com.yolo.demo.config.RabbitConfirmCallbackService;
import com.yolo.demo.config.RabbitReturnCallbackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CallbackController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfirmCallbackService rabbitConfirmCallbackService;

    @Autowired
    private RabbitReturnCallbackService rabbitReturnCallbackService;

    @GetMapping("/callback")
    public void callback() {
        // 全局唯一
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");

        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("callback.exchange", "callback.a.yzm", message, correlationData);
    }

    @GetMapping("/callback2")
    public void callback2() {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("不存在的交换机", "callback.a.yzm", message, correlationData);
    }

    @GetMapping("/callback3")
    public void callback3() {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("callback.exchange", "不存在的路由键", message, correlationData);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "callback_queue"),
            exchange = @Exchange(value = "callback.exchange"),
            key = {"callback.a.yzm", "callback.b.admin"}
    ))
    public void callbackA(Message message) {
        System.out.println(" [ 消费者@A号 ] Received ==> '" + new String(message.getBody()) + "'");
    }

}
