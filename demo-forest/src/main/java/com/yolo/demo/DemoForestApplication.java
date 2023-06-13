package com.yolo.demo;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ForestScan(basePackages = "com.yolo.demo.rpc")// forest扫描远程接口所在的包名
public class DemoForestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoForestApplication.class, args);
    }

}
