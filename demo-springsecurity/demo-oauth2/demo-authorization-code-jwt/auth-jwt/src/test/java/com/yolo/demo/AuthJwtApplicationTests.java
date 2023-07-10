package com.yolo.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthJwtApplicationTests {

    @Test
    void contextLoads() {
        String yoloAppSecret = new BCryptPasswordEncoder().encode("yolo_app_secret");
        System.out.println(yoloAppSecret);
    }

}
