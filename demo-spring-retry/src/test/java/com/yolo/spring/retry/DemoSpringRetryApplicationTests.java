package com.yolo.spring.retry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class DemoSpringRetryApplicationTests {

    @Autowired
    private RetryService retryService;

    @Test
    void testService1() throws IllegalAccessException {
        retryService.service1();
    }

    @Test
    void testService2() throws IllegalAccessException {
        retryService.service2();
    }

    @Test
    void testService3() throws IllegalAccessException {
        retryService.service3();
    }

    @Test
    void testService3_1() throws IllegalAccessException {
        retryService.service3_1();
    }

    @Test
    void testService3_2() throws IllegalAccessException {
        retryService.service3_2();
    }

    @Test
    void testService4() throws MyException {
        retryService.service4("error");
    }

    @Test
    void testService5() throws IllegalAccessException {
        retryService.service5();
    }

    @Test
    void testService6() throws IllegalAccessException {
        retryService.service6();
    }

    @Test
    void testService7() throws IllegalAccessException {
        retryService.service7();
    }

    @Test
    void testService8() throws IllegalAccessException {
        retryService.service8();
    }

    @Test
    void testService9() throws IllegalAccessException {
        retryService.service9();
    }

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



}
