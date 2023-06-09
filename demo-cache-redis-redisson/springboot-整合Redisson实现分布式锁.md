# springboot整合Redisson实现分布式锁

## 一、项目准备

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- springboot整合redisson -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.13.6</version>
        </dependency>
```

### 2、application.yml

```yml

```

### 3、Redissonconfig

```java
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    /**
     * Redisson配置
     */
    @Bean
    public RedissonClient redissonClient() {
        //1、创建配置
        Config config = new Config();

        redisHost = redisHost.startsWith("redis://") ? redisHost : "redis://" + redisHost;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redisHost + ":" + redisPort);

        if (StrUtil.isNotBlank(redisPassword)) {
            serverConfig.setPassword(redisPassword);
        }

        return Redisson.create(config);
    }
    
}
```

```java
//单机
RedissonClient redisson = Redisson.create();
Config config = new Config();
config.useSingleServer().setAddress("myredisserver:6379");
RedissonClient redisson = Redisson.create(config);
 
 
//主从
 
Config config = new Config();
config.useMasterSlaveServers()
    .setMasterAddress("127.0.0.1:6379")
    .addSlaveAddress("127.0.0.1:6389", "127.0.0.1:6332", "127.0.0.1:6419")
    .addSlaveAddress("127.0.0.1:6399");
RedissonClient redisson = Redisson.create(config);
 
 
//哨兵
Config config = new Config();
config.useSentinelServers()
    .setMasterName("mymaster")
    .addSentinelAddress("127.0.0.1:26389", "127.0.0.1:26379")
    .addSentinelAddress("127.0.0.1:26319");
RedissonClient redisson = Redisson.create(config);
 
 
//集群
Config config = new Config();
config.useClusterServers()
    .setScanInterval(2000) // cluster state scan interval in milliseconds
    .addNodeAddress("127.0.0.1:7000", "127.0.0.1:7001")
    .addNodeAddress("127.0.0.1:7002");
RedissonClient redisson = Redisson.create(config);
```

### 4、RedissonUtils

```java
package com.yolo.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedissonUtils {


    private static RedissonClient redissonClient;

    public RedissonUtils(RedissonClient redissonClient) {
        RedissonUtils.redissonClient = redissonClient;
    }

    /**
     * 锁前缀
     */
    private final static String DEFAULT_LOCK_NAME = "nlx-instance";


    /**
     * 加锁（可重入），会一直等待获取锁，不会中断
     *
     * @param lockName 锁名字
     * @param timeout  超时
     * @return boolean ture加锁成功
     */
    public static boolean lock(String lockName, long timeout) {
        checkRedissonClient();
        RLock lock = getLock(lockName);
        try {
            if(timeout != -1){
                // timeout:超时时间   TimeUnit.SECONDS：单位
                lock.lock(timeout, TimeUnit.SECONDS);
            }else{
                lock.lock();
            }
            log.debug(" get lock success ,lockKey:{}", lockName);
            return true;
        } catch (Exception e) {
            log.error(" get lock fail,lockKey:{}, cause:{} ",
                    lockName, e.getMessage());
            return false;
        }
    }


    /**
     * 可中断锁
     * @param lockName 锁名称
     * @param waitTimeout  等待时长
     * @param unit 时间单位
     * @return boolean ture加锁成功
     */
    public static boolean tryLock(String lockName, long waitTimeout, TimeUnit unit) {
        checkRedissonClient();
        RLock lock = getLock(lockName);
        try {
            boolean res = lock.tryLock(waitTimeout,unit);
            if (!res) {
                log.debug(" get lock fail ,lockKey:{}", lockName);
                return false;
            }
            log.debug(" get lock success ,lockKey:{}", lockName);
            return true;
        } catch (Exception e) {
            log.error(" get lock fail,lockKey:{}, cause:{} ",
                    lockName, e.getMessage());
            return false;
        }
    }

    /**
     * 公平锁
     * @param lockName 锁名称
     * @param waitTimeout 等待时长
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return boolean ture加锁成功
     */
    public static boolean getFairLock(String lockName, long waitTimeout,long timeout, TimeUnit unit){
        checkRedissonClient();
        RLock lock = redissonClient.getFairLock(DEFAULT_LOCK_NAME + lockName);
        try {
            //尝试加锁，最多等待(waitTimeout)100秒，上锁后(timeout)10秒自动解锁
            boolean res = lock.tryLock(waitTimeout,timeout,unit);
            if (!res) {
                log.debug(" get lock fail ,lockKey:{}", lockName);
                return false;
            }
            log.debug(" get lock success ,lockKey:{}", lockName);
            return true;
        } catch (Exception e) {
            log.error(" get lock fail,lockKey:{}, cause:{} ",
                    lockName, e.getMessage());
            return false;
        }
    }

    /**
     * 获取读写锁
     *
     * @param lockName 锁名字
     * @return {@link RReadWriteLock}
     */
    public static RReadWriteLock getReadWriteLock(String lockName) {
        return redissonClient.getReadWriteLock(lockName);

    }

    /**
     * 信号量
     */
    public static RSemaphore getSemaphore(String semaphoreName) {
        return redissonClient.getSemaphore(semaphoreName);
    }

    /**
     * 可过期性信号量
     */
    public static RPermitExpirableSemaphore getPermitExpirableSemaphore(String permitExpirableSemaphoreName) {
        return redissonClient.getPermitExpirableSemaphore(permitExpirableSemaphoreName);
    }

    /**
     * 闭锁
     */
    public static RCountDownLatch getCountDownLatch(String countDownLatchName) {
        return redissonClient.getCountDownLatch(countDownLatchName);
    }


    /**
     * 解锁
     */
    public static void unlock(String lockName){
        checkRedissonClient();
        try {
            RLock lock = getLock(lockName);
            //lock.isHeldByCurrentThread()的作用是查询当前线程是否保持此锁定
            //lock.hasQueueThread(Thread thread)的作用是判断当前线程是否处于等待lock的状态
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();
                log.debug("key：{}，unlock success",lockName);
            }else{
                log.debug("key：{}，没有加锁或者不是当前线程加的锁 ",lockName);
            }
        }catch (Exception e){
            log.error("key：{}，unlock error,reason:{}",lockName,e.getMessage());
        }
    }

    public static RLock getLock(String lockName) {
        String key = DEFAULT_LOCK_NAME + lockName;
        return redissonClient.getLock(key);
    }


    private static void checkRedissonClient() {
        if (null == redissonClient) {
            log.error(" redissonClient is null ,please check redis instance ! ");
            throw new RuntimeException("redissonClient is null ,please check redis instance !");
        }
        if (redissonClient.isShutdown()) {
            log.error(" Redisson instance has been shut down !!!");
            throw new RuntimeException("Redisson instance has been shut down !!!");
        }
    }

}
```

## 二、rediison分布式锁

### 1、可重入锁（Reentrant Lock），不可中断(一直等待)

**测试类**

在单元测试中，使用了两个线程同时去获取锁：hello-test，获取到锁的线程休眠5秒，然后释放锁资源。

CountDownLatch：线程同步，为了能在main函数执行结束之前看到连个子线程的执行结果

```java
    private CountDownLatch count = new CountDownLatch(2);

    @Test
    public void test1(){
        String lockName = "hello-test";


        new Thread(() ->{

            String threadName = Thread.currentThread().getName();
            log.info("线程：{} 正在尝试获取锁。。。",threadName);
            boolean lock = RedissonUtils.lock(lockName, 60L);
            doSomething(lock,lockName,threadName);

        }).start();

        new Thread(() ->{
            String threadName = Thread.currentThread().getName();
            log.info("线程：{} 正在尝试获取锁。。。",threadName);
            boolean lock = RedissonUtils.lock(lockName, 60L);
            doSomething(lock,lockName,threadName);
        }).start();

        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("子线程都已执行完毕，main函数可以结束了！");
    }

    private void doSomething(boolean lock,String lockName,String threadName) {
        if(lock){
            log.info("线程：{}，获取到了锁",threadName);
            try{
                try {
                    TimeUnit.SECONDS.sleep(5L);
                    count.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                RedissonUtils.unlock(lockName);
                log.info("线程：{}，释放了锁",threadName);
            }
        }
    }
```

![image-20230609095136172](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609095136172.png)

我们可以发现，Thread-3 在09:25:05.099 获取到了锁，而这个时候 Thread-2 还处于阻塞状态，直到5秒之后 09:25:10.133 Thread-3释放了锁，Thread-2 在 09:25:10.141 获取到了锁，Thread-2 大概阻塞了5秒钟，可以理解为 Thread-2 一直在等待锁资源的释放，如果只有锁的线程一直不释放锁，那么 Thread-4 将会一直处于等待状态。除非有其他线程执行 Thread-2 线程的 interrupted()方法，否则 它的等待将用于休止。

> 我们将这种无休止的等待称为：不可中断，我们使用 RLock 中的lock() 方法的特性就是不可中断

我们的例子中是设置的锁的过期时间，他还支持不设置过期时间，这种情况下，只要程序不解锁，那么其他线程都将一直处于阻塞状态，这样就会引发一个很严重的问题，那就是在线程获取到了锁之后，程序或者服务器突然宕机，等重启完成之后，其他线程也会一直处于阻塞状态，因为宕机前获取的锁还没有被释放

redisson也为我们考虑到了这个问题，所以它设置一个看门狗。它的作用是在Redisson实例被关闭前，不断地延长锁的有效期。默认情况下，看门狗的检查锁的超时时间是30秒钟，也可以通过修改Config.lockWatchdogTimeout来另行指定。

### 2.可重入锁（Reentrant Lock），可中断(到时间结束等待)

```java
boolean tryLock();

	boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
	
	boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

	RFuture<Boolean> tryLockAsync(long waitTime, long leaseTime, TimeUnit unit);

	RFuture<Boolean> tryLockAsync();

	RFuture<Boolean> tryLockAsync(long threadId);

	 RFuture<Boolean> tryLockAsync(long waitTime, TimeUnit unit);

	 RFuture<Boolean> tryLockAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId);
```

tryLock()：很好理解，尝试着加锁，这里面有几个参数讲解一下：

- time：等待锁的最长时间。
- unit：时间单位。
- waitTime：与time一致，等待锁的最长时间。
- leaseTime：锁的过期时间。
- threadId：线程id。

> 大致意思说的就是一个线程带等待 time/waitTime时长后如果还没有获取到锁，那么当前线程将会放弃获取锁资源的机会，去干其他事情。Async结尾的几个方法主要就是异步加锁的意思

```java
    @Test
    public void test2(){
        String lockName = "hello-test1";
        new Thread(() ->{

            String threadName = Thread.currentThread().getName();
            log.info("线程：{} 正在尝试获取锁。。。",threadName);
            boolean lock = RedissonUtils.tryLock(lockName, 2L,TimeUnit.SECONDS);
            doSomething2(lock,lockName,threadName);
        }).start();

        new Thread(() ->{

            String threadName = Thread.currentThread().getName();
            log.info("线程：{} 正在尝试获取锁。。。",threadName);
            boolean lock = RedissonUtils.tryLock(lockName, 2L,TimeUnit.SECONDS);
            doSomething2(lock,lockName,threadName);
        }).start();
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("子线程都已执行完毕，main函数可以结束了！");
    }

    private void doSomething2(boolean lock,String lockName,String threadName) {
        if(lock){
            log.info("线程：{}，获取到了锁",threadName);
            try{
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                RedissonUtils.unlock(lockName);
                log.info("线程：{}，释放了锁",threadName);
            }
        }else{
            log.info("线程：{}，没有获取到锁，过了等待时长，结束等待",threadName);
        }
        count.countDown();
    }
```

![image-20230609103709083](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609103709083.png)

Thread-2 在 10:35:04.145 的时候尝试获取锁，10:35:04.187 的时候获取到了锁，并且进入到了休眠状态，Thread-3 在 10:35:04.145 的时候尝试获取锁，直到 10:35:06.168 也没有获取到，然后 Thread-3就放弃了等待，直接结束了线程，期间花费了两秒钟的时间，而我们设置的等待时间刚好就是两秒，所以单元测试通过

### 3.公平锁（Fair Lock）

它保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程。所有请求线程会在一个队列中排队，当某个线程出现宕机时，Redisson 会等待5秒后继续下一个线程，也就是说如果前面有5个线程都处于等待状态，那么后面的线程会等待至少25秒

```java
@Test
    public void testFairLock() {
        CountDownLatch countDown = new CountDownLatch(3);

        String lockName = "hello-test";
        new Thread(() -> {
            log.info("进入thread1 ======");
            log.info("thread1 正在尝试获取锁。。。");
            boolean lock = RedissonUtils.getFairLock(lockName, 20L, 7L,TimeUnit.SECONDS);
            doSomething3(lock, lockName, "thread1",countDown);
        }).start();

        new Thread(() -> {
            log.info("进入thread2 ======");
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("thread2 休眠结束 正在尝试获取锁。。。");
            boolean lock = RedissonUtils.getFairLock(lockName, 20L,7L, TimeUnit.SECONDS);
            doSomething3(lock, lockName, "thread2",countDown);
        }).start();


        new Thread(() -> {
            log.info("进入thread3 ======");
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("thread3 休眠结束 正在尝试获取锁。。。");
            boolean lock = RedissonUtils.getFairLock(lockName, 20L,7L, TimeUnit.SECONDS);
            doSomething3(lock, lockName, "thread3",countDown);
        }).start();

        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("子线程都已执行完毕，main函数可以结束了！");
    }

    private void doSomething3(boolean lock,String lockName,String threadName,CountDownLatch countDown) {
        if(lock){
            log.info("线程：{}，获取到了锁",threadName);
            try{
                countDown.countDown();
            }finally {
                RedissonUtils.unlock(lockName);
                log.info("线程：{}，释放了锁",threadName);
            }
        }else{
            log.info("线程：{}，没有获取到锁，过了等待时长，结束等待",threadName);
        }

    }
```

![image-20230609105745184](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609105745184.png)

### 4.联锁（MultiLock）

联锁指的是：同时对多个资源进行加锁操作，只有所有资源都加锁成功的时候，联锁才会成功。

```java
    @Test
    public void testMultiLock(){
        RLock lock1 = RedissonUtils.getLock("lock1" );
        RLock lock2 = RedissonUtils.getLock("lock2");
        RLock lock3 = RedissonUtils.getLock("lock3");
        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);
        boolean flag = lock.tryLock();
        if(flag){
            try {
                log.info("联锁加索成功");
            }finally {
                //一定要释放锁
                lock.unlock();
            }
        }
    }
```

### 5.红锁（RedLock）

与联锁比较相似，都是对多个资源进行加锁，但是红锁与连锁不同的是，红锁只需要在大部分资源加锁成功即可

```java
    /**
     * 红锁
     */
    @Test
    public void testRedLock(){
        RLock lock1 = RedissonUtils.getLock("lock1" );
        RLock lock2 = RedissonUtils.getLock("lock2");
        RLock lock3 = RedissonUtils.getLock("lock3");
        RedissonRedLock lock = new RedissonRedLock (lock1, lock2, lock3);
        boolean flag = lock.tryLock();
        if(flag){
            try {
                log.info("红锁加索成功");
            }finally {
                //一定要释放锁
                lock.unlock();
            }
        }
    }
```

### 6.读写锁（ReadWriteLock）

分布式可重入读写锁允许同时有多个读锁和一个写锁处于加锁状态。这点相当于java并发sdk并发包中的 StampedLock

```java
RReadWriteLock rwlock = redisson.getReadWriteLock("testRWLock");
// 最常见的使用方法
rwlock.readLock().lock();
// 或
rwlock.writeLock().lock();
```

另外Redisson还通过加锁的方法提供了leaseTime的参数来指定加锁的时间。超过这个时间后锁便自动解开了

```java
// 10秒钟以后自动解锁
// 无需调用unlock方法手动解锁
rwlock.readLock().lock(10, TimeUnit.SECONDS);
// 或
rwlock.writeLock().lock(10, TimeUnit.SECONDS);

// 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
boolean res = rwlock.readLock().tryLock(100, 10, TimeUnit.SECONDS);
// 或
boolean res = rwlock.writeLock().tryLock(100, 10, TimeUnit.SECONDS);
...
lock.unlock();
```

redis中添加一条数据

![image-20230609112519981](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609112519981.png)

添加写锁，允许写入数据

```java
    @Test
    public void testWriteLock(){
        String s = "";
        RReadWriteLock readWriteLock = RedissonUtils.getReadWriteLock("testRWLock");
        RLock rLock = readWriteLock.writeLock();
        try {
            //1、改数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeValue", s);
            Thread.sleep(30000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        log.info("写入redis中的数据为{}",s);
    }
```

![image-20230609112822795](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609112822795.png)

添加读锁，运行读取数据

```java
    @Test
    public void testReadLock(){
        String s = "";
        RReadWriteLock readWriteLock = RedissonUtils.getReadWriteLock("testRWLock");
        RLock rLock = readWriteLock.readLock();
        try {
            //1、改数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        log.info("读取redis中的数据为{}",s);
    }
```

### 7.信号量（Semaphore）

基于Redis的Redisson的分布式信号量（Semaphore）Java对象RSemaphore采用了与java.util.concurrent.Semaphore相似的接口和用法。同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口

```java
    /**
     * 信号量
     */
    @Test
    public void testSemaphore() throws InterruptedException {
        RSemaphore semaphore = RedissonUtils.getSemaphore("testSemaphore");
        //设置许可个数
        semaphore.trySetPermits(10);
//        //设置许可个数 异步
//        semaphore.acquireAsync();
//        //获取5个许可
//        semaphore.acquire(5);
//        //尝试获取一个许可
//        semaphore.tryAcquire();
//        //尝试获取一个许可 异步
//        semaphore.tryAcquireAsync();
//        //尝试获取一个许可 等待5秒如果未获取到，则返回false
//        semaphore.tryAcquire(5, TimeUnit.SECONDS);
//        //尝试获取一个许可 等待5秒如果未获取到，则返回false 异步
//        semaphore.tryAcquireAsync(5, TimeUnit.SECONDS);
//        //释放一个许可，将其返回给信号量
//        semaphore.release();
//        //释放 6 个许可 ，将其返回给信号量
//        semaphore.release(6);
//        //释放一个许可，将其返回给信号量 异步
//        semaphore.releaseAsync();

        CountDownLatch count = new CountDownLatch(10);
        for (int i= 0;i< 15 ;++i){
            new Thread(() -> {
                try {
                    String threadName = Thread.currentThread().getName();
                    log.info("线程：{} 尝试获取许可。。。。。。。。。。。。。",threadName);
                    //默认获取一个许可，如果没有获取到，则阻塞线程
                    semaphore.acquire();
                    log.info("线程：{}获取许可成功。。。。。。。", threadName);
                    count.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        count.await();
    }
```

可以看到只有十个线程获取许可成功。

![image-20230609113642028](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230609113642028.png)

在实现信号量的时候一定要注意许可数量，如果被使用完，而你用完之后并没有将许可归还给信号量，那么有可能在许可用完之后，之后的线程一直处于阻塞阶段

```java
    /**
     * 信号量
     */
    @Test
    public void testPermitExpirableSemaphore() throws InterruptedException {
        RPermitExpirableSemaphore semaphore = RedissonUtils.getPermitExpirableSemaphore("testPermitExpirableSemaphore");
        //设置许可个数
        semaphore.trySetPermits(10);
        // 获取一个信号，有效期只有2秒钟。
        String permitId = semaphore.acquire(1, TimeUnit.SECONDS);
        log.info("许可：{}",permitId);
        semaphore.release(permitId);
    }
```

### 8.闭锁（CountDownLatch）

基于Redisson的Redisson分布式闭锁（CountDownLatch）Java对象RCountDownLatch采用了与java.util.concurrent.CountDownLatch相似的接口和用法。

我在例子中也是用到了java sdk并发包中的 CountDownLatch ，主要是线程同步的作用，redisson同样也实现了这样的功能

```java
    @Test
    public void testCountDownLatch() throws InterruptedException {
        RCountDownLatch latch = RedissonUtils.getCountDownLatch("testCountDownLatch");
        latch.trySetCount(2);
        new Thread(() ->{
            log.info("这是一个服务的线程");
            try {
                TimeUnit.SECONDS.sleep(3);
                log.info("线程：{}，休眠结束",Thread.currentThread().getName());
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() ->{
            log.info("这是另外一个服务的线程");
            try {
                TimeUnit.SECONDS.sleep(3);
                log.info("线程：{}，休眠结束",Thread.currentThread().getName());
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        latch.await();
        log.info("子线程执行结束。。。。。。");
    }
```

