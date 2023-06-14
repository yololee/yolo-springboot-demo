# rabbitmq-重试机制

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

### 2、配置类

```java
package com.yolo.demo.config;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitMQConfiguration {


    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        String rabbitmqHost = "127.0.0.1";
        String rabbitmqPort = "5672";
        String rabbitmqUsername = "admin";
        String rabbitmqPassword = "admin";
        String rabbitmqVirtualHost = "my_vhost";
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(Integer.parseInt(rabbitmqPort));
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        connectionFactory.setVirtualHost(rabbitmqVirtualHost);
//        connectionFactory.setPublisherReturns(true);//开启return模式
//        connectionFactory.setPublisherConfirms(true);//开启confirm模式
        return connectionFactory;
    }


    @Bean(name = "rabbitTemplate")
    //必须是prototype类型
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(rabbitConnectionFactory());
    }

    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置线程数
        factory.setConcurrentConsumers(1);
        //最大线程数
        factory.setMaxConcurrentConsumers(1);
        //设置为手动确认MANUAL(手动),AUTO(自动);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        // 设置prefetch
        factory.setPrefetchCount(1);
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}

```

## 二、案例

```java
package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RetryController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/retry")
    public void retry() {
        String message = "Hello World !";
        rabbitTemplate.convertAndSend("retry_exchange", "retry_key", message);
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
    }

    private int count = 1;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry_a", durable = "true"),
            exchange = @Exchange(value = "retry_exchange", type = ExchangeTypes.DIRECT, durable = "true"),
            key = "retry_key"
    ))
    public void retry(Message message) {
        log.info("当前执行次数：{}", count++);
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        // 制造异常
        int i = 1 / 0;
        log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));
    }
}

```

启动测试：

无限循环报错
停止后，消息重回Ready状态

![image-20230614134825368](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614134825368.png)

## 三、实现消息重试

### 实现重试

```yml
spring:
  # RabbitMQ配置
  rabbitmq:
    listener:
      simple:
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

> 重启测试
>
> 第一次执行时间2s，第二次4s，第三次8s，第四次16s，第五次由于设置了最大间隔为10s，所有变成了10s
>
> 最后查看retry_a队列，消息没有了，也就是说重试五次失败之后就会移除该消息
>
> 移除操作是由日志中的这个类处理：RejectAndDontRequeueRecoverer(拒绝和不要重新排队)

![image-20230614141931609](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614141931609.png)

### 对重试失败的消息重新排队

```java
    @Bean
    public MessageRecoverer messageRecoverer() {
        return new ImmediateRequeueMessageRecoverer();
    }
```

> 重启运行：
>
> 可以看出：重试5次之后，返回队列，然后再重试5次，周而复始直到不抛出异常为止，这样还是会影响后续的消息消费

![image-20230614142329953](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614142329953.png)

### 把重试失败消息放入重试失败队列

```java
    //@Bean  这个注释掉
    public MessageRecoverer messageRecoverer() {
        return new ImmediateRequeueMessageRecoverer();
    }

    public static final String RETRY_FAILURE_KEY = "retry.failure.key";
    public static final String RETRY_EXCHANGE = "retry_exchange";

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        // 需要配置交换机和绑定键
        return new RepublishMessageRecoverer(rabbitTemplate, RETRY_EXCHANGE, RETRY_FAILURE_KEY);
    }
```

**创建重试失败消息监听**

```java
package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RetryController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/retry")
    public void retry() {
        String message = "Hello World !";
        rabbitTemplate.convertAndSend("retry_exchange", "retry_key", message);
        System.out.println(" [ 生产者 ] Sent ==> '" + message + "'");
    }

    private int count = 1;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry_a", durable = "true"),
            exchange = @Exchange(value = "retry_exchange", type = ExchangeTypes.DIRECT, durable = "true"),
            key = "retry_key"
    ))
    public void retry(Message message) {
        log.info("当前执行次数：{}", count++);
        log.info(" [ 消费者@A号 ] 接收到消息 ==> '" + new String(message.getBody()));
        // 制造异常
        int i = 1 / 0;
        log.info(" [ 消费者@A号 ] 消费了消息 ==> '" + new String(message.getBody()));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry_failure_queue"),
            exchange = @Exchange(value = "retry_exchange"),
            key = "retry.failure.key"
    ))
    public void retryFailure(Message message) {
        log.info(" [ 消费者@重试失败号 ] 接收到消息 ==> '" + new String(message.getBody()));
    }
}
```

> 重启，运行结果：
>
> 重试5次之后，将消息 Republishing failed message to exchange ‘retry.exchange’ with routing key retry-key 转发到重试失败队列，由重试失败消费者消费

![image-20230614143232884](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614143232884.png)

