# rabbitmq-消息回调

## 一、前言

### 1、pom.xml

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

### 2、application.yml

```yml
spring:
  # RabbitMQ配置
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    # 填写自己安装rabbitmq时设置的账号密码，默认账号密码为`guest`
    username: admin
    password: admin
    publisher-confirm-type: correlated
    # 开启return模式
    publisher-returns: true
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

## 二、消息回调

- ConfirmCallback：当消息到达交换机触发回调
- ReturnsCallback：消息(带有路由键routingKey)到达交换机，与交换机的所有绑定键进行匹配，匹配不到触发回调

### 重写ConfirmCallback

```java
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
```

### 重写ReturnsCallback

```java
package com.yolo.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitReturnCallbackService implements RabbitTemplate.ReturnCallback{

    /**
     * 消息路由失败，回调
     * 消息(带有路由键routingKey)到达交换机，与交换机的所有绑定键进行匹配，匹配不到触发回调
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("returnedMessage ===> replyCode={} ,replyText={} ,exchange={} ,routingKey={}", replyCode, replyText, exchange, routingKey);
    }
}

```

### 消息发送者和消费者

```java
package com.yolo.demo.controller;

import com.yolo.demo.config.RabbitConfirmCallbackService;
import com.yolo.demo.config.RabbitReturnCallbackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CallbackController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfirmCallbackService rabbitConfirmCallbackService;

    @Autowired
    private RabbitReturnCallbackService rabbitReturnCallbackService;

    @GetMapping("/callback")
    public void callback() {
        // 全局唯一
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");

        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("callback.exchange", "callback.a.yzm", message, correlationData);
    }

    @GetMapping("/callback2")
    public void callback2() {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("不存在的交换机", "callback.a.yzm", message, correlationData);
    }

    @GetMapping("/callback3")
    public void callback3() {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        String message = "Hello world!";
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(rabbitConfirmCallbackService);
        rabbitTemplate.setReturnCallback(rabbitReturnCallbackService);
        rabbitTemplate.convertAndSend("callback.exchange", "不存在的路由键", message, correlationData);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "callback_queue"),
            exchange = @Exchange(value = "callback.exchange"),
            key = {"callback.a.yzm", "callback.b.admin"}
    ))
    public void callbackA(Message message) {
        System.out.println(" [ 消费者@A号 ] Received ==> '" + new String(message.getBody()) + "'");
    }

}

```

### 测试

访问地址：127.0.0.1:8080/callback

消息正确到达交换机触发回调

![image-20230614155522075](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614155522075.png)

访问地址：127.0.0.1:8080/callback2

消息找不到交换机触发回调

![image-20230614155652622](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614155652622.png)

访问地址：127.0.0.1:8080/callback3

消息路由失败触发回调

![image-20230614155715910](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614155715910.png)

## <font color = 'red'>三、注意：</font>

> 若使用 confirm-callback 或 return-callback，需要配置
>
> publisher-confirm-type: correlated
>
> publisher-returns: true

> 使用return-callback时必须设置mandatory为true
>
> 或者在配置中设置rabbitmq.template.mandatory=true

