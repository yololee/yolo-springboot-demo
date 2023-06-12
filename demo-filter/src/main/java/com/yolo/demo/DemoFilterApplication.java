package com.yolo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan// 扫描 Servlet 相关的组件
public class DemoFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoFilterApplication.class, args);
    }

}
