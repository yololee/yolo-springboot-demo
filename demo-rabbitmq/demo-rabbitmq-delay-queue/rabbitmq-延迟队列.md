# rabbitmq-延迟队列

## 一、前言

### 1、延迟队列介绍

一般来说，发布消息之后，会被交换机接收并转发给对应的队列，队列分配给消费者处理，这个过程很快秒级处理；但有时候我们希望发布完消息后，在指定的时间之后再去处理消息，这个时候就需要使用到延时队列；
虽说是延时队列，但其实也只是对死信队列的一种扩展应用罢了

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
spring:
  # RabbitMQ配置
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    # 填写自己安装rabbitmq时设置的账号密码，默认账号密码为`guest`
    username: admin
    password: admin
    virtual-host: my_vhost # 填写自己的虚拟机名，对应可查看 `127.0.0.1:15672/#/users` 下Admin中的`Can access virtual hosts`信息
    listener:
      simple:
        # 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
        acknowledge-mode: manual
        # 最小的消费者数量
        concurrency: 1
        # 最大的消费者数量
        max-concurrency: 1
        # 指定一个请求能处理多少个消息，如果有事务的话，必须大于等于transaction数量.
        prefetch: 1
        retry:
          # 是否开启重试
          enabled: true
          # 最大重试次数
          max-attempts: 5
          # 重试最大间隔时间
          max-interval: 10000
          # 第一次和第二次尝试发布或传递消息之间的间隔（单位：毫秒）
          initial-interval: 2000
          # 间隔时间乘子，间隔时间*乘子=下一次的间隔时间，最大不能超过设置的最大间隔时间
          multiplier: 2
```

## 二、案例(对queue所有消息进行设置)

### 创建生产者和消费者

> 首先还是得创建普通队列，添加参数绑定死信队列同时设置消息过期时间，生产者发布消息到普通队列，而普通队列没有任何消费者来消费，那么消息在普通队列中存活到设定过期时间就被转发到死信队列，由死信队列的消费者消费消息，以此实现延时功能

```java
package com.yolo.demo.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class DL2Controller {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/dl2")
    public void dl() {
        String message = "Hello World222222222!";
        log.info(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.convertAndSend("normal_exchange2", "normal_key2", message);
    }

    // 监听 normal_queue 正常队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "normal_queue2",declare = "true"//指定一下队列名，默认持久队列，不指定则为临时队列
                    ,arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dlx_exchange2"), //指定一下死信交换机
                    @Argument(name = "x-dead-letter-routing-key",value = "dead_key2"),  //指定死信交换机的路由key
                    @Argument(name = "x-message-ttl",value = "3000",type = "java.lang.Long") //指定队列的过期时间，type需要指定为Long,否则会抛异常
                    //,@Argument(name = "x-max-length",value = "3") //指定队列最大长度，超过会被投入死信，至于type是否需要指定为Long，本人没试过
            }
            ),
            exchange = @Exchange(value = "normal_exchange2",type = ExchangeTypes.DIRECT,durable = "true"),//Exchang的默认类型就是direct，所以type可以不写
            key = "normal_key2"
    ))
    public void normal(Message message, Channel channel) throws IOException {
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        /*
         * 拒绝消息
         * deliveryTag：该消息的index
         * multiple: ture确认本条消息以及之前没有确认的消息(批量)，false仅确认本条消息
         * requeue: true该条消息重新返回MQ queue，MQ broker将会重新发送该条消息
         */
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    }

    // 监听死信队列
    @RabbitListener(bindings = @QueueBinding(
                    value = @Queue(value = "dlx_queue2"),
                    exchange = @Exchange(value = "dlx_exchange2"),
                    key = "dead_key2"
            ))
    public void dl(Message message, Channel channel) throws IOException {
        log.info(" [ 消费者@死信号 ] 接收到消息 ==> '" + new String(message.getBody()));
        //打印完直接丢弃消息
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
    }
}

```

### 测试结果

> 这里我们开启手动ack，然后在普通队列中拒绝ack并重新返回队列，当消息在队列时间超过3s，就会进入延迟队列

![image-20230614152801155](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614152801155.png)

![image-20230614152839367](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614152839367.png)

## 三、案例(对queue每一条消息消息进行设置)

### 创建生产者和消费者

```java
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
```

![image-20230614153212109](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614153212109.png)

