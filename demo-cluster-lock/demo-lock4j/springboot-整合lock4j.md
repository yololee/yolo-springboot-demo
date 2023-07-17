# springboot-整合lock4j

## 一、介绍

lock4j是一个分布式锁组件，其提供了多种不同的支持以满足不同性能和环境的需求。立志打造一个简单但富有内涵的分布式锁组件

1. 简单易用，功能强大，扩展性强。
2. 支持redission,redisTemplate,zookeeper。可混用，支持扩展

gitee地址：https://gitee.com/baomidou/lock4j

## 二、使用前准备

### 引入依赖

```xml
<!-- Lock4j -->
<!-- 若使用redisTemplate作为分布式锁底层，则需要引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>lock4j-redis-template-spring-boot-starter</artifactId>
    <version>2.2.5</version>
</dependency>
<!-- 若使用redisson作为分布式锁底层，则需要引入 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>lock4j-redisson-spring-boot-starter</artifactId>
    <version>2.2.5</version>
</dependency>
```

### 配置文件

```yml
spring:
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
```

## 三、注解属性介绍

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Lock4j {
    String name() default "";

    Class<? extends LockExecutor> executor() default LockExecutor.class;

    String[] keys() default {""};

    long expire() default -1L;

    long acquireTimeout() default -1L;

    boolean autoRelease() default true;
}
```

| @Lock4j注解属性    | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| **name**           | 需要锁住的`key`名称                                          |
| **executor**       | 可以通过该参数设置自定义特定的执行器                         |
| **keys**           | 需要锁住的`keys`名称，可以是多个                             |
| **expire**         | 锁过期时间，主要是用来防止死锁                               |
| **acquireTimeout** | 可以理解为排队等待时长，超过这个时长就退出排队，并排除获取锁超时异常 |
| **autoRelease**    | 是否自动释放锁，默认是`true`                                 |

> @Lock4j(keys = {"#key"}, acquireTimeout = 1000, expire = 10000)
>
> 过期时间是10s
>
> 等待时间是1s

## 四、简单实用

```java
@RestController
public class MockController {
 
    @GetMapping("/lockMethod")
    @Lock4j(keys = {"#key"}, acquireTimeout = 1000, expire = 10000)
    public Map<String,String> lockMethod(@RequestParam String key) {
        ThreadUtil.sleep(5000);
        Map<String,String> map = new HashMap<>();
        map.put("key",key);
        return map;
    }
}
```

打开浏览器窗口，重复刷新访问：http://localhost:8080/lockMethod?key=123

成功获得锁访问结果：

![image-20230717160252961](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230717160252961.png)

抢占不到锁，`Lock4j`会抛出`com.baomidou.lock.exception.LockFailureException: request failed,please retry it.`异常

![image-20230717160350486](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230717160350486.png)

## 五、高级实用

### 自定义执行器

在注解上直接指定特定的执行器：`@Lock4j(executor = CustomRedissonLockExecutor.class)`。

```java
@Component
public class CustomRedissonLockExecutor extends AbstractLockExecutor {
    
    @Override
    public Object acquire(String lockKey, String lockValue, long expire, long acquireTimeout) {
        return null;
    }
 
    @Override
    public boolean releaseLock(String key, String value, Object lockInstance) {
        return false;
    }
}
```

### 自定义锁key生成器

默认的锁key生成器为 `com.baomidou.lock.DefaultLockKeyBuilder`

```java
@Component
public class MyLockKeyBuilder extends DefaultLockKeyBuilder {

    @Override
	public String buildKey(MethodInvocation invocation, String[] definitionKeys) {
		String key = super.buildKey(invocation, definitionKeys);
        // do something
		return key;
	}
}
```

### 自定义锁获取失败策略

```java
@Component
public class GrabLockFailureStrategy implements LockFailureStrategy {
 
    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
 
    }
}
```

### 手动上锁解锁

```java

@Service
public class ProgrammaticService {
    @Autowired
    private LockTemplate lockTemplate;

    public void programmaticLock(String userId) {
        // 各种查询操作 不上锁
        // ...
        // 获取锁
        final LockInfo lockInfo = lockTemplate.lock(userId, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new RuntimeException("业务处理中,请稍后再试");
        }
        // 获取锁成功，处理业务
        try {
            System.out.println("执行简单方法1 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        //结束
    }
}
```

### 指定时间内不释放锁(限流)

```java

@Service
public class DemoService {

    // 用户在5秒内只能访问1次
    @Lock4j(keys = {"#user.id"}, acquireTimeout = 0, expire = 5000, autoRelease = false)
    public Boolean test(User user) {
        return "true";
    }
}
```

