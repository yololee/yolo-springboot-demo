# Rabbitmq-保证消息100%投递成功并被消费

## 一、准备

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!--MySQL 5.1.47-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--druid 数据库连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.24</version>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.4.5</version>
        </dependency>
```

### 2、application.yml

<font color = 'blue'>这里加载多个配置文件，其余配置文件默认以`application-`开头</font>

```yml
server:
  port: 8381

spring:
  profiles:
    active:
      - mysql
      - rabbitmq
```

> mysql配置(文件名为application-mysql.yml)

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
    username: root
    password: root
    druid:
      initialSize: 5
      minIdle: 5
      maxActive: 20
      # 配置获取连接等待超时的时间
      maxWait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      timeBetweenEvictionRunsMillis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      minEvictableIdleTimeMillis: 300000
      validationQuery: select 'x'
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      # 打开PSCache，并且指定每个连接上PSCache的大小
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      filters: stat,wall,slf4j
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000

mybatis-plus:
  #mybatis配置文件
  #config-location: classpath:mybatis-config.xml
  # mapper映射位置
  mapper-locations: classpath:/mapper/**Mapper.xml
  #所有domain别名类所在包
  type-aliases-package: com.yolo.demo.domain
  configuration:
    # 用来打印sql日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    #类属性与表字段的驼峰映射，mybatiplus默认true开启，mybatis需要手动配置，且config-location和configuration不能同时出现
    map-underscore-to-camel-case: true
  #全局配置
  global-config:
    #数据库配置
    db-config:
      #主键策略
      id-type: ASSIGN_ID  # IdType默认的全局
      #表名前缀为tb_，表名为前缀拼接类名（小写）
      #      table-prefix: tb_
      logic-delete-field: removed # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: -1 # 逻辑已删除值(默认为 -1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

> rabbitmq配置(文件名为application-rabbitmq.yml)

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
    # 发送方消息确认（ACK）
    publisher-confirm-type: correlated # 确认消息已发送到交换机(Exchange) [Producer -> Exchange]
    # 开启return模式
    publisher-returns: true
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
          # 第一次和第二次尝试发布或传递消息之间的间隔（单位：毫秒）
          initial-interval: 30000
```

### 3、表结构

```sql
CREATE TABLE `msg_log` (
  `id` varchar(255) NOT NULL DEFAULT '' COMMENT '消息唯一标识',
  `msg` text COMMENT '消息体, json格式化',
  `exchange` varchar(255) NOT NULL DEFAULT '' COMMENT '交换机',
  `routing_key` varchar(255) NOT NULL DEFAULT '' COMMENT '路由键',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态: 0投递中 1投递成功 2投递失败 3已消费',
  `try_count` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_try_time` bigint(20) DEFAULT NULL COMMENT '下一次重试时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_msg_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息投递日志';
```

### 4、消息状态枚举

```java
package com.yolo.demo.common;


public enum MsgLogStatusEnum {

    /**
     * 投递中
     **/
    DELIVERING(0, "delivering"),
    /**
     * 投递成功
     **/
    DELIVER_SUCCESS(1, "deliver_success"),
    /**
     * 投递失败
     **/
    DELIVER_FAIL(2, "deliver_fail"),

    /**
     * 消费成功
     **/
    CONSUMED_SUCCESS(3, "consumed_success");


    private final int key;
    private final String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    MsgLogStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
```

### 5、mq工具类

```java
package com.yolo.demo.util;


import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqUtil implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendWithConfirm(String exchange, String routingKey, Object msgData) {
        log.info("[MQ生产者] 发送方确认模式 交换机:[{}] 路由key:[{}] 发送消息:[{}]", exchange, routingKey, JSONUtil.toJsonStr(msgData));
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.convertAndSend(exchange, routingKey, msgData, new CorrelationData(UUID.randomUUID().toString()));
    }

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

### 6、发送消息

```java
package com.yolo.demo.controller;


import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rabbitmq.client.Channel;
import com.yolo.demo.common.MsgLogStatusEnum;
import com.yolo.demo.domain.MsgLog;
import com.yolo.demo.domain.User;
import com.yolo.demo.mapper.MsgLogMapper;
import com.yolo.demo.util.MqUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class MsgController {

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    private MqUtil mqUtil;


    @PostMapping("/send")
    public void demo(@RequestBody User user){
        String msgId =  UUID.randomUUID().toString();
        MsgLog msgLog = MsgLog.builder().id(msgId)
                .msg(JSONUtil.toJsonStr(user))
                .exchange("userExchange")
                .routingKey("user")
                .status(MsgLogStatusEnum.DELIVERING.getKey())
                .tryCount(0)
                .nextTryTime(System.currentTimeMillis() +  2 * 60 * 1000L)//30s后再投递
                .createTime(System.currentTimeMillis())
                .updateTime(0L)
                .build();

        msgLogMapper.insert(msgLog);

        mqUtil.sendWithConfirm("userExchange","user",JSONUtil.toJsonStr(msgLog));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("userDirectQueue"),
            exchange = @Exchange(value = "userExchange", type = ExchangeTypes.DIRECT),
            key = "user"

    ))
    public void process(@Payload String msg, Channel channel, Message message) {
        MsgLog msgLog = JSONUtil.toBean(msg, MsgLog.class);
        MsgLog msgLogOld = msgLogMapper.selectById(msgLog.getId());
        //保证消息幂等性
        if (null == msgLogOld || msgLogOld.getStatus().equals(MsgLogStatusEnum.CONSUMED_SUCCESS.getKey())) {
            log.info("重复消费, msgId: {}", msgLog.getId());
            return;
        }
        try {
            //具体的业务逻辑
            handleBusinessLogic();

            //处理业务逻辑完成修改msg_log表的状态为成功
//            updateMsgStatus(msgLog);
            //手动确认消息
            //第一个参数，交付标签，相当于消息ID 64位的长整数(从1开始递增)
            // 第二个参数，false表示仅确认提供的交付标签；true表示批量确认所有消息(消息ID小于自身的ID)，包括提供的交付标签
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("告警消息手动确认：" + message.getMessageProperties().getDeliveryTag());
        } catch (Exception e) {
            log.error("告警消息处理过程中发生错误,错误消息是：" + e);
            msgAckHandlerException(message,channel);
        }
    }

    private void updateMsgStatus(MsgLog msgLog) {
        LambdaUpdateWrapper<MsgLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MsgLog::getId,msgLog.getId());
        updateWrapper.set(MsgLog::getStatus,MsgLogStatusEnum.CONSUMED_SUCCESS.getKey());
        updateWrapper.set(MsgLog::getUpdateTime,System.currentTimeMillis());
        msgLogMapper.update(null,updateWrapper);
    }

    private void handleBusinessLogic() {
        log.info("开始处理具体的业务逻辑");
//        int num = 1/0;
        log.info("结束处理具体的业务逻辑");
    }

    private void msgAckHandlerException(Message message, Channel channel) {
        //获取消息中是否再次投递，true:再次投递
        Boolean redelivered = message.getMessageProperties().getRedelivered();
        if (redelivered) {
            log.error("告警消息已重复处理失败,拒绝再次接收...");
            try {
                // 拒绝消息方式二
                // 第一个参数，交付标签
                // 第二个参数，false表示直接丢弃消息，true表示重新排队
                // 跟basicNack的区别就是始终只拒绝提供的交付标签
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else {
            log.error("告警消息即将再次返回队列处理...");
            try {
                // 拒绝消息方式一
                // 第一个参数，交付标签
                // 第二个参数，false表示仅拒绝提供的交付标签；true表示批量拒绝所有消息，包括提供的交付标签
                // 第三个参数，false表示直接丢弃消息，true表示重新排队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

```

### 7、重新投递发送失败

```java
package com.yolo.demo.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.common.MsgLogStatusEnum;
import com.yolo.demo.domain.MsgLog;
import com.yolo.demo.mapper.MsgLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ResendMsg {

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 最大投递次数
    private static final int MAX_TRY_COUNT = 3;

    /**
     * 每30s拉取投递失败的消息, 重新投递
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void resend() {
        log.info("开始执行定时任务(重新投递消息)");
        long currentTimeMillis = System.currentTimeMillis();
        long startTime = currentTimeMillis - 5 * 60 * 1000;

        LambdaQueryWrapper<MsgLog> queryWrapper = Wrappers.<MsgLog>lambdaQuery()
                .eq(MsgLog::getStatus, 0)
                .le(MsgLog::getNextTryTime, currentTimeMillis)
                .ge(MsgLog::getNextTryTime, startTime);

        List<MsgLog> msgLogs = msgLogMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(msgLogs)) {
            log.info("开始执行定时任务(重新投递消息),数据为空终止执行.");
            return;
        }
        msgLogs.forEach(msgLog -> {
            String msgId = msgLog.getId();
            if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                LambdaUpdateWrapper<MsgLog> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(MsgLog::getId,msgLog.getId());
                updateWrapper.set(MsgLog::getStatus,MsgLogStatusEnum.DELIVER_FAIL.getKey());
                updateWrapper.set(MsgLog::getUpdateTime,currentTimeMillis);
                msgLogMapper.update(null,updateWrapper);
                log.info("超过最大重试次数, 消息投递失败, msgId: {}", msgId);
            } else {
                int count = msgLog.getTryCount() + 1;
                msgLog.setTryCount(count);
                Long nextTryTime = msgLog.getNextTryTime();
                if (nextTryTime == 0) {
                    nextTryTime = currentTimeMillis;
                }
                msgLog.setNextTryTime(nextTryTime + count * 60 * 1000L);
                msgLog.setUpdateTime(currentTimeMillis);
                msgLogMapper.updateById(msgLog);

                // 重新投递
                rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), JSONUtil.toJsonStr(msgLog), new CorrelationData(msgId));

                log.info("第 " + (msgLog.getTryCount()) + " 次重新投递消息: " + msgId);
            }
        });

        log.info("定时任务执行结束(重新投递消息)");
    }

}

```

### 8、msg_log 中很老的数据

```java
package com.yolo.demo.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.domain.MsgLog;
import com.yolo.demo.mapper.MsgLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 清除 mysql 表 msg_log 中很老的数据
 * msg_log 保存的是告警消息日志，为防止特殊情况消息丢失，所以消息来了之后就最先保存到mysql中，
 * 时间长了之后，该表数据会特别多，影响性能，而且表中的告警消息已经过了失效性，可以清除
 * 因此暂定 清理 创建时间距离现在超过一个月 的告警消息日志
 */
@Component
@Slf4j
public class ClearMsgLogTask {
    @Autowired
    private MsgLogMapper msgLogMapper;

    @Scheduled(cron = "0 0 19 * * ?") //线上:凌晨3点执行
    // @Scheduled(cron = "0 */1 * * * ?") //测试:每分钟执行一次
    public void executeClearMsgLogTask() {

        long currentTimeMillis = System.currentTimeMillis();
        long minTime = currentTimeMillis - 30 * 24 * 3600 * 1000L;

        List<MsgLog> msgLogs = msgLogMapper.selectList(Wrappers.<MsgLog>lambdaQuery().lt(MsgLog::getCreateTime, minTime));

        if (CollUtil.isNotEmpty(msgLogs)) {
            List<String> collect = msgLogs.stream().filter(Objects::nonNull).map(MsgLog::getId).collect(Collectors.toList());
            List<List<String>> split = ListUtil.split(collect, 1000);
            if (CollUtil.isNotEmpty(split)) {
                split.stream().filter(Objects::nonNull).forEach(s -> {
                    msgLogMapper.deleteBatchIds(s);
                });
            }
        }
        //  logger.info("清理msg_log告警日志表任务完成================================================================");
    }

}
```

## 二、测试

![image-20230615102938026](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615102938026.png)

### 1、正常流程测试

**发送请求**

![image-20230615102715465](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615102715465.png)

**后台日志**

![image-20230615102749820](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615102749820.png)

**数据库消息记录**

状态为3, 表明已消费, 消息重试次数为0, 表明一次投递就成功了

![image-20230615102812723](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615102812723.png)

### 2、验证消息发送到Exchange失败情况下的回调

> 随便指定一个不存在的交换机名称, 请求接口, 看是否会触发回调

![image-20230615103044421](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615103044421.png)

![image-20230615103226003](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615103226003.png)

发送失败, 原因：reply-code=404, reply-text=NOT_FOUND - no exchange 'zhangsan' in vhost 'my_vhost', class-id=60, method-id=40

该回调能够保证消息正确发送到Exchange, 测试完成

### 3、验证消息从Exchange路由到Queue失败情况下的回调

> 同理, 修改一下路由键为不存在的即可, 路由失败, 触发回调

![image-20230615103654127](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615103654127.png)

![image-20230615104728820](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615104728820.png)

发生失败原因：replyCode=312 ,replyText=NO_ROUTE ,exchange=userExchange ,routingKey=lisi

### 4、验证在手动ack模式下, 消费端必须进行手动确认(ack), 否则消息会一直保存在队列中, 直到被消费

![image-20230615114135260](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615114135260.png)

可以看到, 虽然消息确实被消费了, 但是由于是手动确认模式, 而最后又没手动确认, 所以, 消息仍被rabbitmq保存, 所以, 手动ack能够保证消息一定被消费, 但一定要记得basicAck

![image-20230615105156626](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615105156626.png)

### 5、验证消费端幂等性

接着上一步, 去掉注释, 重启服务器, 由于有一条未被ack的消息, 所以重启后监听到消息, 进行消费, 但是由于消费前会判断该消息的状态是否未被消费, 发现status=3, 即已消费, 所以, 直接return, 这样就保证了消费端的幂等性, 即使由于网络等原因投递成功而未触发回调, 从而多次投递, 也不会重复消费进而发生业务异常

![image-20230615110736327](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615110736327.png)

### 6、验证消费端发生异常消息也不会丢失

很显然, 消费端代码可能发生异常, 如果不做处理, 业务没正确执行, 消息却不见了, 给我们感觉就是消息丢失了, 由于我们消费端代码做了异常捕获, 业务异常时, 会触发: channel.basicNack(tag, false, true);, 这样会告诉rabbitmq该消息消费失败, 需要重新入队, 可以重新投递到其他正常的消费端进行消费, 从而保证消息不被丢失

![image-20230615114037870](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615114037870.png)

![image-20230615114221875](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615114221875.png)

可以看到, 由于channel.basicNack(tag, false, true), 未被ack的消息(unacked)会重新入队并被消费, 这样就保证了消息不会走丢

一直错误的话，他会一直发送消息，这样不太好，我们可以设置重试，当重试多少次后，然后丢弃，可是我们mysql中的msg_log表中还有记录，在重新投递三次，如果三次失败就显示这条消息发送失败了。

### 7、验证定时任务的消息重投

实际应用场景中, 可能由于网络原因, 或者消息未被持久化MQ就宕机了, 使得投递确认的回调方法ConfirmCallback没有被执行, 从而导致数据库该消息状态一直是投递中的状态, 此时就需要进行消息重投, 即使也许消息已经被消费了

定时任务只是保证消息100%投递成功, 而多次投递的消费幂等性需要消费端自己保证

我们可以将回调和消费成功后更新消息状态的代码注释掉, 开启定时任务, 查看是否重投

![image-20230615123536446](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615123536446.png)

![image-20230615123558977](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230615123558977.png)