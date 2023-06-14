# rabbitmq-七种工作模型

## 一、简单模式

![image-20230614090127777](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614090127777.png)

在上图的模型中，有一下概念：

- P：生产者，也就是发送消息的程序
- C：消费者，消息的接收者，会一直等待消息的到来
- queue：消息队列，图中红色部分，可以缓存消息，生产者向其中投递消息，消费者从中取出消息

```java
@Configuration
public class SimpleRabbitMqConfig {

    @Bean
    public Queue simpleQueue() {
        // durable: true 标识开启消息队列持久化 (队列当中的消息在重启rabbitmq服务的时候还会存在)
        return new Queue(MqConstant.SIMPLE_QUEUE, true);
    }

}
```

```java
@RestController
@RequestMapping("/simple")
@Slf4j
public class SimpleMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        log.info("[生产者] 发送消息: {}", msgContent);
        rabbitTemplate.convertAndSend(MqConstant.SIMPLE_QUEUE, msgContent);
    }


    @RabbitListener(queues = MqConstant.SIMPLE_QUEUE)
    public void listener(String msg) {
        log.info("[消费者] 接收消息: {}", msg);
    }

}
```

## 二、工作队列模式

![image-20230614091941508](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614091941508.png)

work queues与入门程序相比，多了一个消费端，两个消费端共同消费同一个队列中的消息，但是一个消息只能被一个消费者获取。

这个消息模型在Web应用程序中特别有用，可以处理短的HTTP请求窗口中无法处理复杂的任务。

接下来我们来模拟这个流程：

P：生产者：任务的发布者

C1：消费者1：领取任务并且完成任务，假设完成速度较慢（模拟耗时）

C2：消费者2：领取任务并且完成任务，假设完成速度较快

```java
@Configuration
public class WorkRabbitMqConfig {

    @Bean
    public Queue workQueue() {
        // durable: true 标识开启消息队列持久化 (队列当中的消息在重启rabbitmq服务的时候还会存在)
        return new Queue(MqConstant.WORK_QUEUE, true);
    }

}
```

```java
@RestController
@RequestMapping("/work")
@Slf4j
public class WorkMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        IntStream.range(0,10).forEach(num -> {
            rabbitTemplate.convertAndSend(MqConstant.WORK_QUEUE, msgContent + num);
            log.info("[生产者] 发送消息: {}", msgContent + num);
        });
    }


    @RabbitListener(queues = MqConstant.WORK_QUEUE)
    public void listener1(String msg) throws InterruptedException {
        Thread.sleep(200);
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(queues = MqConstant.WORK_QUEUE)
    public void listener2(String msg) throws InterruptedException {
        Thread.sleep(1000);
        log.info("[消费者2] 接收消息: {}", msg);
    }

}
```

## 三、发布订阅模式

**交换机类型**

Exchange类型有以下几种：

Fanout：广播，将消息交给所有绑定到交换机的队列

Direct：定向，把消息交给符合指定routing key 的队列

Topic：通配符，把消息交给符合routing pattern（路由模式） 的队列

Header：header模式与routing不同的地方在于，header模式取消routingkey，使用header中的 key/value（键值对）匹配队列

![image-20230614093102534](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614093102534.png)

1、一个生产者多个消费者
2、每个消费者都有一个自己的队列
3、生产者没有将消息直接发送给队列，而是发送给exchange(交换机、转发器)
4、每个队列都需要绑定到交换机上
5、生产者发送的消息，经过交换机到达队列，实现一个消息被多个消费者消费

```java
@RestController
@RequestMapping("/fanout")
@Slf4j
public class FanoutMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send")
    public void send() {
        String msgContent = "Hello World";
        IntStream.range(0,10).forEach(num -> {
            rabbitTemplate.convertAndSend(MqConstant.FANOUT_EXCHANGE, "",msgContent + num);
            log.info("[生产者] 发送消息: {}", msgContent + num);
        });
    }


    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = MqConstant.FANOUT_QUEUE_1, durable = "true"),
            exchange = @Exchange(value = MqConstant.FANOUT_EXCHANGE,type = ExchangeTypes.FANOUT,durable = "true"))
    })
    public void listener1(String msg) {
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = MqConstant.FANOUT_QUEUE_2, durable = "true"),
            exchange = @Exchange(value = MqConstant.FANOUT_EXCHANGE,type = ExchangeTypes.FANOUT,durable = "true"))
    })
    public void listener2(String msg) {
        log.info("[消费者2] 接收消息: {}", msg);
    }


}
```

> 可以看到俩个消费者都消费啦十条消息

## 四、路由模式

![image-20230614101128117](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614101128117.png)

P：生产者，向Exchange发送消息，发送消息时，会指定一个routing key。

X：Exchange（交换机），接收生产者的消息，然后把消息递交给 与routing key完全匹配的队列

C1：消费者，其所在队列指定了需要routing key 为 error 的消息

C2：消费者，其所在队列指定了需要routing key 为 info、error、warning 的消息

```java
@RestController
@RequestMapping("/direct")
@Slf4j
public class DirectMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send1")
    public void send1() {
        String msgContent = "Hello World1";
        rabbitTemplate.convertAndSend(MqConstant.DIRECT_EXCHANGE, "one",msgContent);
        log.info("[生产者1] 发送消息: {}", msgContent);
    }

    @GetMapping("/send2")
    public void send2() {
        String msgContent = "Hello World2";
        rabbitTemplate.convertAndSend(MqConstant.DIRECT_EXCHANGE, "two",msgContent);
        log.info("[生产者2] 发送消息: {}", msgContent);
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.DIRECT_QUEUE_1, durable = "true"),
            exchange = @Exchange(value = MqConstant.DIRECT_EXCHANGE,type = ExchangeTypes.DIRECT,durable = "true"),
            key = "one")
    )
    public void listener1(String msg) {
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.DIRECT_QUEUE_2, durable = "true"),
            exchange = @Exchange(value = MqConstant.DIRECT_EXCHANGE,type = ExchangeTypes.DIRECT,durable = "true"),
            key = "two")
    )
    public void listener2(String msg) {
        log.info("[消费者2] 接收消息: {}", msg);
    }
}
```

## 五、主题模式(通配符模式)

![image-20230614102259732](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614102259732.png)

每个消费者监听自己的队列，并且设置带统配符的routingkey,生产者将消息发给broker，由交换机根据routingkey来转发消息到指定的队列。

Routingkey一般都是有一个或者多个单词组成，多个单词之间以“.”分割，例如：inform.sms

> **通配符规则：**
>
> \#：匹配一个或多个词
>
> *：匹配不多不少恰好1个词

举例：

audit.#：能够匹配audit.irs.corporate 或者 audit.irs

audit.*：只能匹配audit.irs

```java
@RestController
@RequestMapping("/topic")
@Slf4j
public class TopicMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/send1")
    public void send1() {
        String msgContent = "Hello World1";
        rabbitTemplate.convertAndSend(MqConstant.TOPIC_EXCHANGE, "topic.one",msgContent);
        log.info("[生产者1] 发送消息: {}", msgContent);
    }

    @GetMapping("/send2")
    public void send2() {
        String msgContent = "Hello World2";
        rabbitTemplate.convertAndSend(MqConstant.TOPIC_EXCHANGE, "topic.one.two",msgContent);
        log.info("[生产者2] 发送消息: {}", msgContent);
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.TOPIC_QUEUE_1, durable = "true"),
            exchange = @Exchange(value = MqConstant.TOPIC_EXCHANGE,type = ExchangeTypes.TOPIC,durable = "true"),
            key = "topic.*")
    )
    public void listener1(String msg) {
        log.info("[消费者1] 接收消息: {}", msg);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConstant.TOPIC_QUEUE_2, durable = "true"),
            exchange = @Exchange(value = MqConstant.TOPIC_EXCHANGE,type = ExchangeTypes.TOPIC,durable = "true"),
            key = "topic.#")
    )
    public void listener2(String msg) {
        log.info("[消费者2] 接收消息: {}", msg);
    }
}
```

![image-20230614102630143](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614102630143.png)

可以看到生产1发送消息，俩个消费者都可以接受消息，生产者2发信消息只有消费者2可以消费

## 六、RPC模式

![image-20230614102841734](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230614102841734.png)

**基本概念：**
Callback queue 回调队列，客户端向服务器发送请求，服务器端处理请求后，将其处理结果保存在一个存储体中。而客户端为了获得处理结果，那么客户在向服务器发送请求时，同时发送一个回调队列地址reply_to。

Correlation id 关联标识，客户端可能会发送多个请求给服务器，当服务器处理完后，客户端无法辨别在回调队列中的响应具体和那个请求时对应的。为了处理这种情况，客户端在发送每个请求时，同时会附带一个独有correlation_id属性，这样客户端在回调队列中根据correlation_id字段的值就可以分辨此响应属于哪个请求

**流程说明：**

1. 当客户端启动的时候，它创建一个匿名独享的回调队列。
2. 在 RPC 请求中，客户端发送带有两个属性的消息：一个是设置回调队列的 reply_to 属性，另一个是设置唯一值的 correlation_id 属性。
3. 将请求发送到一个 rpc_queue 队列中。
4. 服务器等待请求发送到这个队列中来。当请求出现的时候，它执行他的工作并且将带有执行结果的消息发送给 reply_to 字段指定的队列。
5. 客户端等待回调队列里的数据。当有消息出现的时候，它会检查 correlation_id 属性。如果此属性的值与请求匹配，将它返回给应用

```java
@Configuration
public class RpcRabbitMqConfig {

    @Bean
    public Queue rpcQueue() {
        return new Queue(MqConstant.RPC_QUEUE);
    }

}
```

```java
@RestController
@RequestMapping("/rpc")
@Slf4j
public class RpcMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send(){
        String msgContent = "Hello World";
        log.info("[生产者] 发送消息: {}", msgContent);
        Object msgObj = this.rabbitTemplate.convertSendAndReceive(MqConstant.RPC_QUEUE, msgContent);
        log.info("[生产者] 接收回应：{}", msgObj);
    }


    @RabbitListener(queues = MqConstant.RPC_QUEUE)
    public String listener(String msg) {
        log.info("[消费者] 接收消息: {}", msg);
        return "消费者back";
    }
}
```

## 七、Publisher Confirms

查看官方文档：https://www.rabbitmq.com/getstarted.html

