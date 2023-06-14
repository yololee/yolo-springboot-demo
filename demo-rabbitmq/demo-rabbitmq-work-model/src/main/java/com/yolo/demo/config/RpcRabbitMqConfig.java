package com.yolo.demo.config;

import com.yolo.demo.common.MqConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcRabbitMqConfig {

    @Bean
    public Queue rpcQueue() {
        return new Queue(MqConstant.RPC_QUEUE);
    }

}