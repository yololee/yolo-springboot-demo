package com.yolo.demo.task;



import com.yolo.demo.anno.SchedulerClusterLock;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ImagesTask {


    /**
     * 发送消息拉取获取镜像
     */
    @SchedulerClusterLock
    @Scheduled(cron="0/30 * * * * ?")//每30s执行一次
    public void send(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

    }

}
