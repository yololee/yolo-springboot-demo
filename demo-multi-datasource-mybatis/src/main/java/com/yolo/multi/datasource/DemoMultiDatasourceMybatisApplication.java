package com.yolo.multi.datasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.yolo.multi.datasource.mapper")
public class DemoMultiDatasourceMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMultiDatasourceMybatisApplication.class, args);
    }

}
