package com.yolo.async.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncTask {

    @SneakyThrows
    @Async(value = "asyncPoolTaskExecutor-1")
    public void doTask1(String name) {
        log.info("{}开始执行，当前线程名称【{}】", name,Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();
        Thread.sleep(2000);
        long t2 = System.currentTimeMillis();
        log.info("task1 cost {} ms" , t2-t1);
    }

    @SneakyThrows
    @Async(value = "asyncPoolTaskExecutor-1")
    public void doTask2(String name) {
        log.info("{}开始执行，当前线程名称【{}】", name,Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();
        Thread.sleep(3000);
        long t2 = System.currentTimeMillis();
        log.info("task2 cost {} ms" , t2-t1);
    }

    @SneakyThrows
    @Async(value = "asyncPoolTaskExecutor-2")
    public void doTask3(String name) {
        log.info("{}开始执行，当前线程名称【{}】", name,Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();
        Thread.sleep(3000);
        long t2 = System.currentTimeMillis();
        log.info("task2 cost {} ms" , t2-t1);
    }
}
