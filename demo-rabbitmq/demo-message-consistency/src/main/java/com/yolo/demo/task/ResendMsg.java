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

        LambdaQueryWrapper<MsgLog> queryWrapper = Wrappers.<MsgLog>lambdaQuery()
                .eq(MsgLog::getStatus, 0)
                .ge(MsgLog::getNextTryTime, currentTimeMillis);

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
