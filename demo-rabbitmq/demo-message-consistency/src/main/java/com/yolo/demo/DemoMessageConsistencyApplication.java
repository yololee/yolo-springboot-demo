package com.yolo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoMessageConsistencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMessageConsistencyApplication.class, args);
    }

}
