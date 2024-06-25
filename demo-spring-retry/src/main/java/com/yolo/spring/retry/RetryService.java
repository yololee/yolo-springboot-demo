package com.yolo.spring.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class RetryService {

    @Retryable(value = IllegalAccessException.class)
    public void service1() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Retryable(include = IllegalAccessException.class, maxAttempts = 5)
    public void service2() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "${maxAttempts}")
    public void service3() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "#{1+1}")
    public void service3_1() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Retryable(value = IllegalAccessException.class, maxAttemptsExpression = "#{${maxAttempts}}")//效果和上面的一样
    public void service3_2() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException("manual exception");
    }

    @Retryable(exclude = MyException.class)
    public void service4(String exceptionMessage) throws MyException {
        log.info("do something... {}", LocalDateTime.now());
        throw new MyException(exceptionMessage);
    }


    @Retryable(value = IllegalAccessException.class,
            backoff = @Backoff(value = 2000))
    public void service5() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }

    @Retryable(value = IllegalAccessException.class,
            backoff = @Backoff(value = 2000,delay = 500))
    public void service6() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }

    @Retryable(value = IllegalAccessException.class,maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void service7() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }

    @Retryable(value = IllegalAccessException.class,maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2,maxDelay = 5000))
    public void service8() throws IllegalAccessException {
        log.info("do something... {}", LocalDateTime.now());
        throw new IllegalAccessException();
    }

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











}
