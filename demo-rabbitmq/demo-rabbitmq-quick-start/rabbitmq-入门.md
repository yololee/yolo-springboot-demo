# rabbitmq-入门

## 一、前言

### 1、rabbitmq介绍

RabbitMQ是由erlang语言开发，基于AMQP（Advanced Message Queue 高级消息队列协议）协议实现的消息队列，它是一种应用程序之间的通信方法，消息队列在分布式系统开发中应用非常广泛

RabbitMQ官方地址：http://www.rabbitmq.com

### 2、rabbitmq工作原理

![image-20230613152908368](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613152908368.png)

**组成部分说明：**

- Broker：消息队列进程，此进程包括俩个部分：Exchange和Queue
- Exchange：消息队列交换机，按一定的规则将消息路由转发到某一个队列，对消息进行过滤
- Queue：消息队列，存储消息的队列，消息到达队列并转发给指定的消费者
- Producer：消息生产者，即生产方客户端，生产方客户端将消息发送
- Consumer：消息消费者，即消费方客户端，接受MQ转发的消息

**生产者发送消息的流程：**

1. 生产者和Broker建立TCP连接
2. 生产者和Broker建立通道
3. 生产者通过通道把消息发送给Broker，由Exchange将消息进行转发
4. Exchange将消息转发到指定的Queue

**消费者接受消息流程：**

1. 消费者和Broker建立TCP连接
2. 消费者和Broker建立通道
3. 消费者监听指定的Queue
4. 当有消息到达Queue时Broker默认把消息推送给消费者
5. 消费者接收到消息
6. ack回复

### 3、部署rabbitmq

[使用docker-compose安装rabbitmq](https://gitee.com/huanglei1111/docker-compose/tree/master/Linux/rabbitmq)

地址：http://127.0.0.1:15672

用户名：admin

密码：admin

![image-20230613153659673](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613153659673.png)

## 二、快速入门

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
    virtual-host: my_vhost # 填写自己的虚拟机名，对应可查看 `127.0.0.1:15672/#/users` 下Admin中的`Can access virtual hosts`信息
    listener:
      simple:
        # 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
        acknowledge-mode: auto
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
          # 第一次和第二次尝试发布或传递消息之间的间隔（单位：毫秒）
          initial-interval: 30000
```

或者自定义rabbitmq全局配置,这俩种选择一个，如果俩个都写啦，则以自定义配置为准

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
        factory.setConcurrentConsumers(1);  //设置线程数
        factory.setMaxConcurrentConsumers(1); //最大线程数
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置为手动确认
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
```



### 3、测试

```java
package com.yolo.demo.controller;

import org.springframework.amqp.core.ExchangeTypes;
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
            value = @Queue(value = "helloWorldQueue",declare = "true"),
            exchange = @Exchange(value = "helloWorldExchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "helloWorld"
    )
    })
    public void receive(String message) {
        System.out.println(" [ 消费者 ] Received ==> '" + message + "'");
    }
}

```

**发送消息测试**

> 访问：http://localhost:8080/send
>
> 这里采用的是自动ack机制

![image-20230613160149419](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613160149419.png)

然后我们打开rabbitmq管理界面

![image-20230613160517467](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613160517467.png)

> Ready：表示待消费数量；队列中拥有可以被消费者消费的消息数量。
> Unacked：表示待确认数量；队列分配消息给消费者时，给该条消息一个待确认状态，当消费者确认消息之后，队列才会移除该条消息。
> Total：表示待消费数和待确认数的总和

## 三、基本消费模型

在新增一个消费者

```java
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "helloWorldQueue",declare = "true"),
            exchange = @Exchange(value = "helloWorldExchange",type = ExchangeTypes.DIRECT,durable = "true"),
            key = "helloWorld"
    )
    })
    public void receive2(Message message) {
        System.out.println(" [ 消费者@2 ] Received ==> '" + new String(message.getBody()) + "'");
    }
```

重启启动，访问接口，进行测试，可以看到消息被平均消费了

![image-20230613164249019](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613164249019.png)

## 四、竞争消费者模式

队列的消息分配方式默认是平均分配，即第一条消息分配给一个消息者，第二条消息就分配给另一个消息者，以此类推…

上面示例有2个消费者监听，由于只是简单的打印语句，所以看不出有什么问题。
我进行修改一下，通过设置线程休眠时间来表示消费者处理消费的任务时间

```java
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
            value = @Queue(value = "helloWorldQueue",declare = "true"),
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
        Thread.sleep(10000);
        System.out.println(" [ 消费者@2 ] Received ==> '" + new String(message.getBody()) + "'");
        System.out.println(" [ 消费者@2号 ] 处理消息数：" + count2++);
    }

}
```

![image-20230613164640097](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613164640097.png)

> 现在就能很明显的看出，消费者1号很快地处理完消息后就处于空闲状态；而消费者2号却一直很忙碌。当消息数量成千上万的时候，由消费者2号处理的消息会堆积很多，达不到时效性

**解决方案**

> 设置prefetch参数=1，实现原理是：队列只会分配一条消息给对应的监听消费者，收到消费者的确认回复之后才会重新分配另一条消息

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
        acknowledge-mode: auto
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
          # 第一次和第二次尝试发布或传递消息之间的间隔（单位：毫秒）
          initial-interval: 30000
```

> <font color = 'red'> 修改最小的消费者数量和最大消费者数量可以让俩个消费者消费的效率差不多，我这里是修改最大消费者数量为10，最小消费者数量为5，然后消费者一号和消费者二号都消费了五个消息</font>

可以看到消费者一号消费了八个，消费者二号消费了俩个

![image-20230613164433048](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230613164433048.png)

> [Gitee项目地址](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-rabbitmq/demo-rabbitmq-quick-start)