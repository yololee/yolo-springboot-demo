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
