package com.yolo.logback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class DemoLogbackApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoLogbackApplication.class, args);
        int length = context.getBeanDefinitionNames().length;
        log.trace("Spring boot启动初始化了 {} 个 Bean", length);
        log.debug("Spring boot启动初始化了 {} 个 Bean", length);
        log.info("Spring boot启动初始化了 {} 个 Bean", length);
        log.warn("Spring boot启动初始化了 {} 个 Bean", length);
        log.error("Spring boot启动初始化了 {} 个 Bean", length);

        String name = "zhangsan";
        Integer age = 18;
        log.info("name:{}, age:{}",name,age);
        log.info("name:" + name + ", age:" + age);



//        try {
//            int i = 0;
//            int j = 1 / i;
//        } catch (Exception e) {
//            log.error("【SpringBootDemoLogbackApplication】启动异常：", e);
//        }
    }

}
