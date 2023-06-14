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

import java.util.stream.IntStream;

@RestController
@RequestMapping("/fanout")
@Slf4j
public class FanoutMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        IntStream.range(0,10).forEach(num -> {
            rabbitTemplate.convertAndSend(MqConstant.FANOUT_EXCHANGE, "",msgContent + num);
            log.info("[生产者] 发送消息: {}", msgContent + num);
        });
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = MqConstant.FANOUT_QUEUE_1, durable = "true"),
            exchange = @Exchange(value = MqConstant.FANOUT_EXCHANGE,type = ExchangeTypes.FANOUT,durable = "true"))
    })
    public void listener1(String msg) {
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = MqConstant.FANOUT_QUEUE_2, durable = "true"),
            exchange = @Exchange(value = MqConstant.FANOUT_EXCHANGE,type = ExchangeTypes.FANOUT,durable = "true"))
    })
    public void listener2(String msg) {
        log.info("[消费者2] 接收消息: {}", msg);
    }


}
