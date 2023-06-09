package com.yolo.demo;


import cn.hutool.core.lang.UUID;
import com.yolo.demo.util.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = DemoCacheRedisRedissonApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DemoCacheRedisRedissonApplicationTests {

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


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 读写锁
     */
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


}
