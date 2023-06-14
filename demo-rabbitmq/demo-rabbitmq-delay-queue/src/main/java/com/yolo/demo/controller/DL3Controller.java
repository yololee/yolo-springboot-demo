package com.yolo.demo.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class DL3Controller {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/dl3")
    public void dl() {
        String s = "Hello World! 3333333333";
        log.info(" [ 生产者 ] Sent ==> '" + s + "'");
        //设置过期时间
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("12000");
        Message message = new Message(s.getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.convertAndSend("normal_exchange3", "normal_key3", message);
    }

    // 监听 normal_queue 正常队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue3",declare = "true"//指定一下队列名，默认持久队列，不指定则为临时队列
                    ,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dlx_exchange3"), //指定一下死信交换机
                    @Argument(name = "x-dead-letter-routing-key",value = "dead_key3"),  //指定死信交换机的路由key
                    //@Argument(name = "x-message-ttl",value = "3000",type = "java.lang.Long") //指定队列的过期时间，type需要指定为Long,否则会抛异常
                    //,@Argument(name = "x-max-length",value = "3") //指定队列最大长度，超过会被投入死信，至于type是否需要指定为Long，本人没试过
            }
            ),
            exchange = @Exchange(value = "normal_exchange3",type = ExchangeTypes.DIRECT,durable = "true"),//Exchang的默认类型就是direct，所以type可以不写
            key = "normal_key3"
    ))
    public void normal(Message message, Channel channel) throws IOException, InterruptedException {
        /*
         * deliveryTag：该消息的index
         * multiple: ture确认本条消息以及之前没有确认的消息(批量)，false仅确认本条消息
         * requeue: true该条消息重新返回MQ queue，MQ broker将会重新发送该条消息
         */
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    }

    // 监听死信队列
    @RabbitListener(bindings = @QueueBinding(
                    value = @Queue(value = "dlx_queue3"),
                    exchange = @Exchange(value = "dlx_exchange3"),//Exchang的默认类型就是direct，所以type可以不写
                    key = "dead_key3"
            ))
    public void dl(Message message, Channel channel) throws IOException {
        log.info(" [ 消费者@死信号 ] 接收到消息 ==> '" + new String(message.getBody()));
        //打印完直接丢弃消息
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
    }
}
