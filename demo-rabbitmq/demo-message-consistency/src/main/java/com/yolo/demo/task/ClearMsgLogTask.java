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
