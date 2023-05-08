package com.yolo.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yolo.mybatis.mapper")
public class DemoOrmMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoOrmMybatisApplication.class, args);
    }

}
