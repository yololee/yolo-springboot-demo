package com.yolo.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Slf4j
@EnableAsync
public class AsyncScheduledTaskConfig {
    /**
     * 1.这种形式的线程池配置是需要在使用的方法上面添加@Async("customAsyncThreadPool")注解的
     * 2。如果在使用的方法上不添加该注解，那么spring就会使用默认的线程池
     * 3.所以如果添加@Async注解但是不指定使用的线程池，又想自己自定义线程池，那么就可以重写spring默认的线程池
     * 4.所以第二个方法就是重写spring默认的线程池
     */
    @Bean("customAsyncThreadPool")
    public Executor customAsyncThreadPool() {

        final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
        log.info("AVAILABLE_PROCESSORS:{}",AVAILABLE_PROCESSORS);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //最大线程数
        executor.setMaxPoolSize(30);
        //核心线程数
        executor.setCorePoolSize(20);
        //任务队列的大小
        executor.setQueueCapacity(1024);
        //线程池名的前缀
        executor.setThreadNamePrefix("asy-task");
        //允许线程的空闲时间60秒
        executor.setKeepAliveSeconds(60);

        //设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);


        /**
         * 拒绝处理策略
         * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
         * AbortPolicy()：直接抛出异常。
         * DiscardPolicy()：直接丢弃。
         * DiscardOldestPolicy()：丢弃队列中最老的任务。
         */
        /**
         * 特殊说明：
         * 1. 这里演示环境，拒绝策略咱们采用抛出异常
         * 2.真实业务场景会把缓存队列的大小会设置大一些，
         * 如果，提交的任务数量超过最大线程数量或将任务环缓存到本地、redis、mysql中,保证消息不丢失
         * 3.如果项目比较大的话，异步通知种类很多的话，建议采用MQ做异步通知方案
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //线程初始化
        executor.initialize();
        return executor;
    }

}
