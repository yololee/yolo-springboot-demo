package com.yolo.demo.aspect;

import com.yolo.demo.anno.SchedulerClusterLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;


/**
 * 集群环境中使用定时任务的问题。通过在redis中加锁来控制同一时间段内仅允许一个定时任务执行
 *
 * @author jujueaoye
 * @date 2023/07/17
 */
@Aspect
@Slf4j
@Component
public class SchedulerClusterLockAspect {


	public final String REDIS_LOCK_PREFIX = "MULTICLOUD:LOCK:";

	@Resource
	private RedissonClient redissonClient;

	@Around(value = "@annotation(com.yolo.demo.anno.SchedulerClusterLock)")
    public Object lock(ProceedingJoinPoint pjp) throws Throwable {
		// 获得所拦截的对象
		Object target = pjp.getTarget();
		// 获得所拦截的方法名
		String methodName = pjp.getSignature().getName();
		// 通过反射，获取无参的public方法对象
		Method method = target.getClass().getMethod(methodName);
		// 判断该方法签名上是否有@ClusterLock
		if (!method.isAnnotationPresent(SchedulerClusterLock.class)) {
			return pjp.proceed();
		}
		// build a lock key
		String lockKey =REDIS_LOCK_PREFIX + target.getClass().getName() + "." + methodName + "()";

		// timer task lock for a cluster environment
		RLock lock = redissonClient.getLock(lockKey);
		//试用redis原子性SETNX来判断是否有锁 使用失效时间， 避免redis长时间内保留锁，造成定时任务无法执行
		try {
			if (lock.tryLock()) {
//				LOG.info("execute method = [{}] start", method);
				Object obj = pjp.proceed();
//				LOG.info("execute method = [{}] end", method);
				return obj;
			} else {
				log.warn("other thread is running, lockKey[{}].", lockKey);
				return null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock.isLocked() && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
    }
	
}

