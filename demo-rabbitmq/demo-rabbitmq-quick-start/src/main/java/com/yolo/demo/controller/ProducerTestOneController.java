package com.yolo.demo.controller;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerTestOneController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send(@RequestParam(value = "message", required = false, defaultValue = "Hello World") String message) {
        for (int i = 1; i <= 10; i++) {
            String msg = message + " ..." + i;
            System.out.println(" [ 生产者 ] Sent ==> '" + msg + "'");
            rabbitTemplate.convertAndSend("helloWorldExchange","helloWorld", msg);
        }
    }

    private int count1=1;
    private int count2=1;

    /**
     * @Queue 注解参数解释
     * 1.value 队列名称
     * 2.durable 是否持久化，如果持久化，mq重启后队列还在
     * 3.exclusive 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
     * 4.autoDelete 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
     * 5.arguments 参数，可以设置一个队列的扩展参数，比如：可设置存活时间
     *
     * @Exchange 注解参数解释
     * 1.exchange，交换机，如果不指定将使用mq的默认交换机（设置为""）
     * 2.type 工作模式，默认工作模式是direct
     * 3.durable 是否持久化
     */
    @RabbitListener(bindings = {@QueueBinding(
            //指定一下队列名，默认持久队列，不指定则为临时队列
            value = @Queue(value = "helloWorldQueue",durable = "true"),
            exchange = @Exchange(value = "helloWorldExchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "helloWorld"
    )
    })
    public void receive(String message) throws InterruptedException {
        Thread.sleep(200);
        System.out.println(" [ 消费者 ] Received ==> '" + message + "'");
        System.out.println(" [ 消费者@1号 ] 处理消息数：" + count1++);
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "helloWorldQueue",declare = "true"),
            exchange = @Exchange(value = "helloWorldExchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "helloWorld"
    )
    })
    public void receive2(Message message) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(" [ 消费者@2 ] Received ==> '" + new String(message.getBody()) + "'");
        System.out.println(" [ 消费者@2号 ] 处理消息数：" + count2++);
    }

}
