package com.example.easy.es;

import cn.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EsMapperScan("com.example.easy.es.mapper")
public class DemoElasticsearchEasyEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoElasticsearchEasyEsApplication.class, args);
    }

}
