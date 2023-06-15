package com.yolo.demo.util;


import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqUtil implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendWithConfirm(String exchange, String routingKey, Object msgData) {
        log.info("[MQ生产者] 发送方确认模式 交换机:[{}] 路由key:[{}] 发送消息:[{}]", exchange, routingKey, JSONUtil.toJsonStr(msgData));
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.convertAndSend(exchange, routingKey, msgData, new CorrelationData(UUID.randomUUID().toString()));
    }

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

    /**
     * 消息路由失败，回调
     * 消息(带有路由键routingKey)到达交换机，与交换机的所有绑定键进行匹配，匹配不到触发回调
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("returnedMessage ===> replyCode={} ,replyText={} ,exchange={} ,routingKey={}", replyCode, replyText, exchange, routingKey);
    }
}