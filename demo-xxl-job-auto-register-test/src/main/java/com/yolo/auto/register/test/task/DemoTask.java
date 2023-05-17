package com.yolo.auto.register.test.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yolo.auto.register.annotation.XxlRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务
 */
@Slf4j
@Component
public class DemoTask {

    @XxlJob(value = "demo-auto-register-test")
    @XxlRegister(cron = "0/2 * * * * ?", author = "yolo", jobDesc = "测试auto-register")
    public ReturnT<String> execute(String param) throws Exception {
        // 可以动态获取传递过来的参数，根据参数不同，当前调度的任务不同
        log.info("【param】= {}", param);
        log.info("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @XxlJob(value = "demo-test")
    @XxlRegister(cron = "0/2 * * * * ?", author = "yolo", jobDesc = "demo-test",triggerStatus = 1)
    public ReturnT<String> execute2(String param) throws Exception {
        // 可以动态获取传递过来的参数，根据参数不同，当前调度的任务不同
        log.info("【param】= {}", param);
        log.info("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }
}
