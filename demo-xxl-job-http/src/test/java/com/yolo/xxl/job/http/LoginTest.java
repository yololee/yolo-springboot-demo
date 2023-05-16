package com.yolo.xxl.job.http;


import com.yolo.xxl.job.http.util.XxlJobApiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginTest extends DemoXxlJobHttpApplicationTests{

    @Autowired
    private XxlJobApiUtils xxlJobApiUtils;

    @Test
    public void testLogin(){

        String cookie = xxlJobApiUtils.getCookie();
        System.out.println(cookie);

    }
}
