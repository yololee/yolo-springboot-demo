# rabbitmq-死信队列

## 一、前言

### 1、死信队列介绍

创建一个普通队列时，通过添加配置绑定另一个交换机(死信交换机)，在普通队列发生异常时，消息就通过死信交换机转发到绑定它的队列里，这个绑定死信交换机的队列就是死信队列

### 2、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

### 3、application.yml

```yml

```

## 二、案例

### 创建生产者和消费者

```java
package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DLController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private int count = 1;

    @GetMapping("/dl")
    public void dl() {
        String message = "Hello World!";
        log.info(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.convertAndSend("normal_exchange", "normal_key", message);
    }



    // 监听 normal_queue 正常队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue",declare = "true"//指定一下队列名，默认持久队列，不指定则为临时队列
                    ,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dlx_exchange"), //指定一下死信交换机
                    @Argument(name = "x-dead-letter-routing-key",value = "dead_key"),  //指定死信交换机的路由key
                    //@Argument(name = "x-message-ttl",value = "3000",type = "java.lang.Long") //指定队列的过期时间，type需要指定为Long,否则会抛异常
                    //,@Argument(name = "x-max-length",value = "3") //指定队列最大长度，超过会被投入死信，至于type是否需要指定为Long，本人没试过
            }
            ),
            exchange = @Exchange(value = "normal_exchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "normal_key"
    ))
    public void normal(Message message)  {
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        log.info("当前执行次数：{}", count++);
        int i = 1 / 0;
        log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));
    }

    // 监听死信队列
    @RabbitListener(bindings = @QueueBinding(
                    value = @Queue(value = "dlx_queue"),
                    exchange = @Exchange(value = "dlx_exchange"),
                    key = "dead_key"
            ))
    public void dl(Message message) {
        log.info(" [ 消费者@死信号 ] 接收到消息 ==> '" + new String(message.getBody()));
    }
}

```

### 测试结果

> 服务器上normal-queue有DLX、DLK标识，说明该队列绑定了死信交换机和路由键；
> 重试5次之后，就将消息转发给死信队列

![image-20230614145705942](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614145705942.png)

### 修改消费者、手动确认

修改确认模式为手动确认

![image-20230614145854802](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614145854802.png)

修改消费者

```java
    // 监听 normal_queue 正常队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue",declare = "true"//指定一下队列名，默认持久队列，不指定则为临时队列
                    ,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dlx_exchange"), //指定一下死信交换机
                    @Argument(name = "x-dead-letter-routing-key",value = "dead_key"),  //指定死信交换机的路由key
                    //@Argument(name = "x-message-ttl",value = "3000",type = "java.lang.Long") //指定队列的过期时间，type需要指定为Long,否则会抛异常
                    //,@Argument(name = "x-max-length",value = "3") //指定队列最大长度，超过会被投入死信，至于type是否需要指定为Long，本人没试过
            }
            ),
            exchange = @Exchange(value = "normal_exchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "normal_key"
    ))
    public void normal(Message message, Channel channel) throws IOException {
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        log.info("当前执行次数：{}", count++);
//        int i = 1 / 0;
//        log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));

        try {
            // 制造异常
            int i = 1 / 0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));
        } catch (Exception e) {
            log.info("捕获异常，不会启动重试机制，异常消息直接转发到死信队列");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
```

### 重启测试

![image-20230614150311631](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614150311631.png)

## 三、总结

- 手动确认并且主动捕获了异常是不会触发重试机制，异常消息直接转发到死信队列
- 死信队列是针对某个队列发生异常时进行处理
- 重试机制中的RepublishMessageRecoverer是对所有队列发生异常时进行处理，并且优先于死信队列