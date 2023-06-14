package com.yolo.demo.controller;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/ack")
public class AckSenderController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send(@RequestParam(value = "message", required = false, defaultValue = "Hello World") String message) {

        IntStream.range(1, 11).forEach(i -> {
            String msg = message + " ..." + i;
            System.out.println(" [ 生产者 ] Sent ==> '" + msg + "'");
            rabbitTemplate.convertAndSend("helloWorldExchange", "ack", msg);
        });
    }

    private int count1 = 1;
//    private int count2 = 1;
//    private int count3 = 1;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "ackQueue", durable = "true"),
//            exchange = @Exchange(value = "helloWorldExchange", type = ExchangeTypes.DIRECT, durable = "true"),
//            key = "ack"
//    ))
//    public void receive(Message message, Channel channel) throws InterruptedException, IOException {
//        Thread.sleep(200);
//        System.out.println(" [ 消费者@1号 ] Received ==> '" + new String(message.getBody()) + "'");
//        System.out.println(" [ 消费者@1号 ] 处理消息数：" + count1++);
//        // 确认消息
//        // 第一个参数，交付标签，相当于消息ID 64位的长整数(从1开始递增)
//        // 第二个参数，false表示仅确认提供的交付标签；true表示批量确认所有消息(消息ID小于自身的ID)，包括提供的交付标签
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "ackQueue", durable = "true"),
//            exchange = @Exchange(value = "helloWorldExchange", type = ExchangeTypes.DIRECT, durable = "true"),
//            key = "ack"
//    ))
//    public void receive2(Message message, Channel channel, @Headers Map<String, Object> map) throws InterruptedException, IOException {
//        Thread.sleep(600);
//        System.out.println(" [ 消费者@2号 ] Received ==> '" + new String(message.getBody()) + "'");
//        System.out.println(" [ 消费者@2号 ] 处理消息数：" + count2++);
//
//        // 确认消息
//        channel.basicAck((Long) map.get(AmqpHeaders.DELIVERY_TAG), false);
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "ackQueue", durable = "true"),
//            exchange = @Exchange(value = "helloWorldExchange", type = ExchangeTypes.DIRECT, durable = "true"),
//            key = "ack"
//    ))
//    public void receive3(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws InterruptedException, IOException {
//        Thread.sleep(1000);
//        System.out.println(" [ 消费者@3号 ] Received ==> '" + new String(message.getBody()) + "'");
//        System.out.println(" [ 消费者@3号 ] 处理消息数：" + count3++);
//
//        // 确认消息
//        channel.basicAck(deliveryTag, false);
//    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ackQueue",durable = "true"
            ),
            exchange = @Exchange(value = "helloWorldExchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "ack"
    ))
    public void receive4(Message message, Channel channel) throws IOException, InterruptedException {
        Thread.sleep(200);
        System.out.println(" [ 消费者@4号 ] Received ==> '" + new String(message.getBody()) + "'");
        System.out.println(" [ 消费者@4号 ] 消息被我拒绝了：" + count1++);

        // 拒绝消息方式一
        // 第一个参数，交付标签
        // 第二个参数，false表示仅拒绝提供的交付标签；true表示批量拒绝所有消息，包括提供的交付标签
        // 第三个参数，false表示直接丢弃消息，true表示重新排队
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

        // 拒绝消息方式二
        // 第一个参数，交付标签
        // 第二个参数，false表示直接丢弃消息，true表示重新排队
        // 跟basicNack的区别就是始终只拒绝提供的交付标签
//        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

}
