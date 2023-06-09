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
