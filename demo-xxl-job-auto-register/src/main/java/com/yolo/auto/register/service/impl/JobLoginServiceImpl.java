package com.yolo.auto.register.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yolo.auto.register.service.JobLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class JobLoginServiceImpl implements JobLoginService {

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    @Value("${xxl.job.admin.username}")
    private String username;

    @Value("${xxl.job.admin.password}")
    private String password;

    private final Map<String,String> loginCookie=new HashMap<>();

    @Override
    public void login() {
        String url=adminAddress+"/login";
        HttpResponse response = HttpRequest.post(url)
                .form("userName",username)
                .form("password",password)
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> "XXL_JOB_LOGIN_IDENTITY".equals(cookie.getName())).findFirst();
        if (!cookieOpt.isPresent()) {
            throw new RuntimeException("get xxl-job cookie error!");
        }

        String value = cookieOpt.get().getValue();
        loginCookie.put("XXL_JOB_LOGIN_IDENTITY",value);
    }

    @Override
    public String getCookie() {
        for (int i = 0; i < 3; i++) {
            String cookieStr = loginCookie.get("XXL_JOB_LOGIN_IDENTITY");
            if (cookieStr !=null) {
                return "XXL_JOB_LOGIN_IDENTITY="+cookieStr;
            }
            login();
        }
        throw new RuntimeException("get xxl-job cookie error!");
    }


}