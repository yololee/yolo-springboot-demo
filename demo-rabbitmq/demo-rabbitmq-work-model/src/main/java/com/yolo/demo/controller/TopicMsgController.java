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
@RequestMapping("/topic")
@Slf4j
public class TopicMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send1")
    public void send1() {
        String msgContent = "Hello World1";
        rabbitTemplate.convertAndSend(MqConstant.TOPIC_EXCHANGE, "topic.one",msgContent);
        log.info("[生产者1] 发送消息: {}", msgContent);
    }

    @GetMapping("/send2")
    public void send2() {
        String msgContent = "Hello World2";
        rabbitTemplate.convertAndSend(MqConstant.TOPIC_EXCHANGE, "topic.one.two",msgContent);
        log.info("[生产者2] 发送消息: {}", msgContent);
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.TOPIC_QUEUE_1, durable = "true"),
            exchange = @Exchange(value = MqConstant.TOPIC_EXCHANGE,type = ExchangeTypes.TOPIC,durable = "true"),
            key = "topic.*")
    )
    public void listener1(String msg) {
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.TOPIC_QUEUE_2, durable = "true"),
            exchange = @Exchange(value = MqConstant.TOPIC_EXCHANGE,type = ExchangeTypes.TOPIC,durable = "true"),
            key = "topic.#")
    )
    public void listener2(String msg) {
        log.info("[消费者2] 接收消息: {}", msg);
    }
}
