package com.yolo.sso.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoServerApplication.class, args);
        System.out.println("\n------ Sa-Token-SSO 统一认证中心启动成功 ");
    }

}
