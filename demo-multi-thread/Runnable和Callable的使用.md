# Runnable和Callable的使用

## 线程池配置

```java
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

```

## Runnable

### 使用线程池批量插入

```java
    @Resource()
    @Qualifier("customAsyncThreadPool")
    private Executor executor;

    @Autowired
    private CompanyRunnable companyRunnable;

    /**
     * 使用线程池批量插入
     */
    @Test
    public void batchInsert2() {
        long start = System.currentTimeMillis();
        List<Company> companies = IntStream.range(0, 10000).mapToObj(s -> Company.builder().name("华为").contact("lisi" + s).contactType("phone").build()).collect(Collectors.toList());
        List<List<Company>> split = ListUtil.split(companies, 100);
        try {
            for (List<Company> companyList : split) {
                //创建一个类,里面执行具体的逻辑
                executor.execute(() -> companyRunnable.batchSave(companyList));
//                executor.execute(() -> companyService.saveBatch(companyList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量插入失败");
        }
        long end = System.currentTimeMillis();
        log.info("一次性插入一万条耗时{}毫秒：" , end - start );
    }
```

```java
@Component
public class CompanyRunnable {

    @Autowired
    private CompanyService companyService;

    public void batchSave(List<Company> companyList){
        try {
            companyService.saveBatch(companyList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量插入失败");
        }

    }
}
```

### CompletableFuture自定义线程池批量插入

```java
    /**
     * 使用stream配合线程池批量插入
     */
    @Test
    public void batchInsert4() {
        long start = System.currentTimeMillis();
        List<Company> companies = IntStream.range(0, 10000).mapToObj(s -> Company.builder().name("华为").contact("lisi" + s).contactType("phone").build()).collect(Collectors.toList());
        List<List<Company>> split = ListUtil.split(companies, 100);
        asyncCallable1(split);
        long end = System.currentTimeMillis();
        log.info("一次性插入一万条耗时{}毫秒：" , end - start );
    }

    public <P> void asyncCallable1(List<P> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(i -> CompletableFuture.runAsync(() -> {
                try {
                    companyRunnable.batchSave((List<Company>) i);
                } catch (Exception e) {
                    log.error("Exception:" + e);
                }
            }, executor));
        }
    }
```

## Callable

### 使用线程池批量查询

```java
    @Test
    public void query2() throws Exception {
        long start = System.currentTimeMillis();
        List<Company> list = companyService.list();

        List<QueryTask> tasks = new ArrayList<>();
        for (Company company : list) {
            QueryTask queryTask = new QueryTask();
            queryTask.setCompany(company);
            tasks.add(queryTask);
        }

        List<CompanyVO> voList = ThreadUtil.executeCompletionService(tasks);

        log.info("查询出来的条数：{}" ,voList.size() );
        long end = System.currentTimeMillis();
        log.info("查询耗时{}毫秒：" , end - start );
    }
```

创建多个线程任务，用于多线程执行

```java
public class QueryTask implements Callable<CompanyVO> {
    private Company company;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public CompanyVO call() {
        CompanyVO vo = new CompanyVO();
        BeanUtil.copyProperties(company,vo);
        return vo;
    }
}
```

线程池工具类

```java
package com.yolo.demo.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    static ExecutorService executorService = new ThreadPoolExecutor(2, 3,
            10000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static String simpleName = null;

    public static <V, T extends Callable<V>> List<V> executeCompletionService(List<T> tasks) throws Exception {
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<>(1);
        }
        List<V> result = new ArrayList<>(tasks.size());
        CompletionService<V> completionService = new ExecutorCompletionService<>(executorService);
        for (T task : tasks) {
            Class<? extends Callable> aClass = task.getClass();
            simpleName = aClass.getSimpleName();
            completionService.submit(task);
        }
        Future<V> take = null;
        for (int index = 0; index < tasks.size(); index++) {
            try {
                take = completionService.take();
                V res = take.get();
                if (res != null) {
                    result.add(res);
                }
            } catch (Exception e) {
                if (take != null && !take.isDone()) {
                    take.cancel(true);
                }
                logger.info("simpleName==>" + simpleName);
                logger.warn(simpleName + " execute completion service error, message is " + e.getMessage());
                throw new Exception(e);
            }
        }
        return result;
    }

}
```

### CompletableFuture自定义线程池批量查询

```java
 @Autowired
    private QueryTaskLoader<CompanyVO,Company> queryTaskLoader;

    @Resource()
    @Qualifier("customAsyncThreadPool")
    private Executor executor;

    @Test
    public void query3() throws Exception {
        long start = System.currentTimeMillis();
        List<Company> list = companyService.list();
        List<CompanyVO> voList = asyncCallable(list, queryTaskLoader);
        log.info("查询出来的条数：{}" ,voList.size() );
        long end = System.currentTimeMillis();
        log.info("查询耗时{}毫秒：" , end - start );
    }


    public <R, P> List<R> asyncCallable(List<P> list, QueryTaskLoader<R, P> loader) {
        if (CollectionUtils.isEmpty(list)) {
            return ListUtil.empty();
        }

        return list.stream().map(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return loader.load(i);
                    } catch (Exception e) {
                        log.error("Exception:" + e);
                    }
                    return null;
                }, executor)).map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
    }
```

