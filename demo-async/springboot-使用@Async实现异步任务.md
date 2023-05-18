# springboot-使用@Async实现异步任务

## 一、新建配置类，开启@Async功能支持

```java
package com.yolo.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略
 * 通常有以下四种策略：
 * ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
 * ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
 * ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
 * ThreadPoolExecutor.CallerRunsPolicy：重试添加当前的任务，自动重复调用 execute() 方法，直到成功
 */
@Configuration
@EnableAsync
public class AsyncPoolConfiguration {
    @Bean(name = "asyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor executor1() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(10);
        //线程池维护线程的最大数量,只有在缓冲队列满了之后才会申请超过核心线程数的线程
        taskExecutor.setMaxPoolSize(100);
        //缓存队列
        taskExecutor.setQueueCapacity(50);
        //许的空闲时间,当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        taskExecutor.setKeepAliveSeconds(200);
        //异步方法内部线程名称
        taskExecutor.setThreadNamePrefix("async-1-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean(name = "defaultAsyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor executor2() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(2);
        //线程池维护线程的最大数量,只有在缓冲队列满了之后才会申请超过核心线程数的线程
        taskExecutor.setMaxPoolSize(10);
        //缓存队列
        taskExecutor.setQueueCapacity(50);
        //许的空闲时间,当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        taskExecutor.setKeepAliveSeconds(200);
        //异步方法内部线程名称
        taskExecutor.setThreadNamePrefix("async-2-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}


```

## 二、在方法上标记异步调用

> 每一个@Async可以指定不同的线程池

```java
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
```

### 三、测试

```java
    @Autowired
    private AsyncTask asyncTask;

    @Test
    public void contextLoads() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        asyncTask.doTask1("task1");
        asyncTask.doTask2("task2");
        asyncTask.doTask3("task3");
        Thread.sleep(1000);
        long t2 = System.currentTimeMillis();
        log.info("main cost {} ms", t2-t1);

    }
```

![image-20230517165131940](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517165131940.png)

> [Gitee项目地址（demo-async）](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-async)