package com.yolo.jasypt;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DemoJasyptApplication.class)
@RunWith(SpringRunner.class)
public class DemoJasyptApplicationTests {


    @Value("${yolo.datasource.password}")
    private String password;

    @Test
    public void contextLoads() {
        System.out.println(password);
    }

}
