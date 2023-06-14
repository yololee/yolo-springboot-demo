package com.yolo.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitConfirmCallbackService implements RabbitTemplate.ConfirmCallback {

    /**
     * 消息到达交换机触发回调
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            log.error("消息发送异常! correlationData={} ,ack={}, cause={}", correlationData.getId(), ack, cause);
        }else {
            log.info("消息发送成功");
        }
    }
}
