package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RetryController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/retry")
    public void retry() {
        String message = "Hello World !";
        rabbitTemplate.convertAndSend("retry_exchange", "retry_key", message);
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
    }

    private int count = 1;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry_a", durable = "true"),
            exchange = @Exchange(value = "retry_exchange", type = ExchangeTypes.DIRECT, durable = "true"),
            key = "retry_key"
    ))
    public void retry(Message message) {
        log.info("当前执行次数：{}", count++);
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        // 制造异常
        int i = 1 / 0;
        log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry_failure_queue"),
            exchange = @Exchange(value = "retry_exchange"),
            key = "retry.failure.key"
    ))
    public void retryFailure(Message message) {
        log.info(" [ 消费者@重试失败号 ] 接收到消息 ==> '" + new String(message.getBody()));
    }
}
