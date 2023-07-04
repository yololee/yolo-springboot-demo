package com.yolo.demo.config.security.login;


import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  认证权限入口 - 未登录的情况下访问所有接口都会拦截到此
 */
@Slf4j
@Component
public class AdminAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        log.error(e.getMessage());
        ResponseUtils.out(response, ApiResponse.ofException("未登录！！！"));
    }

}
