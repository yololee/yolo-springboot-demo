# springboot-自定义注解结合redis实现分布式定时任务锁

### 1、pom文件

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.11.1</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- 对象池，使用redis时必须引入 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
```

### 2、application.yml配置文件

```yml
spring:
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
```

### 3、redisson配置文件

```java
package com.yolo.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedissonConfigure {

    @Resource
    private RedisProperties redisProperties;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (redisProperties.getSentinel() != null && !CollectionUtils.isEmpty(redisProperties.getSentinel().getNodes())) {
            Set<String> sentinels = new HashSet<>();
            for (String url : redisProperties.getSentinel().getNodes()) {
                String sentinel = "redis://" + url;
                sentinels.add(sentinel);
            }
            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(sentinels.toArray(new String[sentinels.size() - 1]))
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .setReadMode(ReadMode.MASTER);
            serverConfig.setDatabase(0);
            return Redisson.create(config);
        } else {
            config.useSingleServer()
                    .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
            config.setCodec(new JsonJacksonCodec());
            return Redisson.create(config);

        }
    }

}
```

### 4、自定义注解

```java
package com.yolo.demo.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 分布式定时任务方法锁
 *
 * @author jujueaoye
 * @date 2023/07/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SchedulerClusterLock {
}

```

### 5、结合aop实现逻辑

```java
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


```

### 6、启动类

**@EnableScheduling**不要忘记啦

```java
@SpringBootApplication
@EnableScheduling
public class DemoAnnoRedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAnnoRedisLockApplication.class, args);
    }

}
```

### 7、测试

```java
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
```

