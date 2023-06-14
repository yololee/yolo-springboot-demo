package com.yolo.demo.controller;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 单个消息设置手动确认
 *
 * @author jujueaoye
 * @date 2023/06/14
 */
@Slf4j
@RestController
@RequestMapping("/mq")
public class AckController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("ack2")
    public String ack2() {
        String msgContent = "Hello World " + DateTime.now();
        log.info("{} [生产者] 发送消息: {}", DateTime.now(), msgContent);
        this.rabbitTemplate.convertAndSend("test_exchange", "test_ack_manual_routing_key", msgContent);
        return "SUCCESS";
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("test_ack_manual_queue");
        // 指定ack模式
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            log.info("----------------------------------------------");
            log.info("{} [消费者] deliveryTag:{} 接收消息: {}", DateTime.now(), deliveryTag, new String(message.getBody()));
            if (message.getMessageProperties().getHeaders().get("error") == null) {
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, false);
            }
        });
        return container;
    }

}