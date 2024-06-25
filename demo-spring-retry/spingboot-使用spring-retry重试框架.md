# Spring Boot中使用Spring-Retry重试框架

## maven 依赖

```xml
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>1.3.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>
```

## 注解使用

### 开启Retry功能

在启动类中使用`@EnableRetry`注解

```java
package com.yolo.spring.retry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class DemoSpringRetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringRetryApplication.class, args);
    }

}
```

### 注解`@Retryable`

需要在重试的代码种加入重试注解`Retryable`

默认情况下，会重试3次，间隔1秒（这个我们可以从注释中看到）

`Retryable`注解的maxAttempts 参数值默认为 3

`Backoff`注解的value参数默认值为 1

```java
@Service
@Slf4j
public class RetryService {

    @Retryable(value = IllegalAccessException.class)
    public void service1() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }
}
```

编写测试用例

```java
@SpringBootTest
public class DemoSpringRetryApplicationTests {

    @Autowired
    private RetryService retryService;

    @Test
    void testService1() throws IllegalAccessException {
        retryService.service1();
    }

}
```

运行结果如下

运行结果显示重试了三次，其中间隔了一秒钟执行一次

![image-20240625093817140](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625093817140.png)

### `@Retryable`参数介绍

#### `maxAttempts`

用于设置重试次数

```java
    @Retryable(include = IllegalAccessException.class, maxAttempts = 5)
    public void service2() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Test
    void testService2() throws IllegalAccessException {
        retryService.service2();
    }
```

![image-20240625094912135](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625094912135.png)

从运行结果可以看到，方法执行了5次。

#### `maxAttemptsExpression`

```java
    @Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "${maxAttempts}")
    public void service3() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }
```

`maxAttemptsExpression`则可以使用表达式，比如上述就是通过获取配置中maxAttempts的值，我们可以在application.yml设置。上述其实省略掉了SpEL表达式`#{....}`，运行结果的话可以发现方法执行了4次

```yml
maxAttempts: 4
```

我们可以使用SpEL表达式

```java
// 执行了俩次
@Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "#{1+1}")
public void service3_1() throws IllegalAccessException {
    log.info("do something... {}", LocalDateTime.now());
    throw new IllegalAccessException("manual exception");
}

//执行了四次
@Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "#{${maxAttempts}}")//效果和上面的一样
public void service3_2() throws IllegalAccessException {
    log.info("do something... {}", LocalDateTime.now());
    throw new IllegalAccessException("manual exception");
}
```

> 但这里值得注意的是, Spring Retry 1.2.5之后`exceptionExpression`是可以省略掉`#{...}`

#### `exclude`

这个`exclude`属性可以帮我们排除一些我们不想重试的异常

```java
    @Retryable(exclude = MyException.class)
    public void service4(String exceptionMessage) throws MyException {
        log.info("do something... {}", LocalDateTime.now());
        throw new MyException(exceptionMessage);
    }

    @Test
    void testService4() throws MyException {
        retryService.service4("error");
    }
```

![image-20240625100029828](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625100029828.png)

执行了一次，没有重试

#### `backoff`

重试等待策略, 默认使用`@Backoff`注解

`@Backoff`的`value`属性,用于设置重试间隔

```java
    @Retryable(value = IllegalAccessException.class,
            backoff = @Backoff(value = 2000))
    public void service5() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }
```

![image-20240625100329976](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625100329976.png)

运行结果可以看出来重试的间隔为2秒

`@Backoff`的`delay`属性，它与`value`属性不能共存，当`delay`不设置的时候会去读`value`属性设置的值，如果`delay`设置的话则会忽略`value`属性

```java
    @Retryable(value = IllegalAccessException.class,
            backoff = @Backoff(value = 2000,delay = 500))
    public void service6() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }
```

![image-20240625100546035](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625100546035.png)

可以看到运行结果是每500ms 重试一次

`@Backoff`的`multiplier`的属性, 指定延迟倍数, 默认为0

```java
    @Retryable(value = IllegalAccessException.class,maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void service7() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }
```

![image-20240625100823541](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625100823541.png)

可以看不到运行了四次，每次的间隔分别为 2,4,6,8s

`@Backoff`的`maxDelay`属性,设置最大的重试间隔，当超过这个最大的重试间隔的时候，重试的间隔就等于`maxDelay`的值

```java
    @Retryable(value = IllegalAccessException.class,maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2,maxDelay = 5000))
    public void service8() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }
```

![image-20240625103406884](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625103406884.png)

可以最后的最大重试间隔是5秒

## 注解`@Recover`

当`@Retryable`方法重试失败之后，最后就会调用`@Recover`方法。用于`@Retryable`失败时的“兜底”处理方法。 `@Recover`的方法必须要与`@Retryable`注解的方法保持一致，第一入参为要重试的异常，其他参数与`@Retryable`保持一致，返回值也要一样，否则无法执行！

```java
    @Retryable(value = IllegalAccessException.class)
    public void service9() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }
    @Recover
    public void recover9(IllegalAccessException e) {
        log.info("service retry after Recover => {}", e.getMessage());
    }
    @Retryable(value = ArithmeticException.class)
    public int service10() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        return 1 / 0;
    }
    @Recover
    public int recover10(ArithmeticException e) {
        log.info("service retry after Recover => {}", e.getMessage());
        return 0;
    }
    @Retryable(value = ArithmeticException.class)
    public int service11(String message) throws IllegalAccessException {
        log.info("do something... {},{}", message, LocalDateTime.now());
        return 1 / 0;
    }
    @Recover
    public int recover11(ArithmeticException e, String message) {
        log.info("{},service retry after Recover => {}", message, e.getMessage());
        return 0;
    }
```

![image-20240625104012577](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625104012577.png)

## 注解`@CircuitBreaker`

> 熔断模式：指在具体的重试机制下失败后打开断路器，过了一段时间，断路器进入半开状态，允许一个进入重试，若失败再次进入断路器，成功则关闭断路器，注解为`@CircuitBreaker`,具体包括熔断打开时间、重置过期时间

```java
    // openTimeout时间范围内失败maxAttempts次数后，熔断打开resetTimeout时长
    @CircuitBreaker(openTimeout = 1000, resetTimeout = 3000, value = NullPointerException.class)
    public void circuitBreaker(int num) {
        log.info(" 进入断路器方法num={}", num);
        if (num > 8) return;
        Integer n = null;
        System.err.println(1 / n);
    }


    @Recover
    public void recover(NullPointerException e) {
        log.info("service retry after Recover => {}", e.getMessage());
    }
```

测试方法

```java
    @Test
    public void testCircuitBreaker() throws InterruptedException {
        System.err.println("尝试进入断路器方法，并触发异常...");
        retryService.circuitBreaker(1);
        retryService.circuitBreaker(1);
        retryService.circuitBreaker(9);
        retryService.circuitBreaker(9);
        System.err.println("在openTimeout 1秒之内重试次数为2次，未达到触发熔断, 断路器依然闭合...");
        TimeUnit.SECONDS.sleep(1);
        System.err.println("超过openTimeout 1秒之后, 因为未触发熔断，所以重试次数重置，可以正常访问...,继续重试3次方法...");
        retryService.circuitBreaker(1);
        retryService.circuitBreaker(1);
        retryService.circuitBreaker(1);
        System.err.println("在openTimeout 1秒之内重试次数为3次，达到触发熔断，不会执行重试，只会执行恢复方法...");
        retryService.circuitBreaker(1);
        TimeUnit.SECONDS.sleep(2);
        retryService.circuitBreaker(9);
        TimeUnit.SECONDS.sleep(3);
        System.err.println("超过resetTimeout 3秒之后，断路器重新闭合...,可以正常访问");
        retryService.circuitBreaker(9);
        retryService.circuitBreaker(9);
        retryService.circuitBreaker(9);
        retryService.circuitBreaker(9);
        retryService.circuitBreaker(9);
    }
```

运行结果

![image-20240625104400959](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20240625104400959.png)