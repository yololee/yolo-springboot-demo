package com.yolo.demo.config.security.login;

import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *   认证失败处理 - 前后端分离情况下返回json数据格式
 */
@Slf4j
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ApiResponse apiResponse;
        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            apiResponse = ApiResponse.ofException(e.getMessage());
        } else if (e instanceof LockedException) {
            apiResponse = ApiResponse.ofException("账户被锁定，请联系管理员!");
        } else if (e instanceof CredentialsExpiredException) {
            apiResponse = ApiResponse.ofException("证书过期，请联系管理员!");
        } else if (e instanceof AccountExpiredException) {
            apiResponse = ApiResponse.ofException("账户过期，请联系管理员!");
        } else if (e instanceof DisabledException) {
            apiResponse = ApiResponse.ofException("账户被禁用，请联系管理员!");
        } else {
            log.error("登录失败：", e);
            apiResponse = ApiResponse.ofException("登录失败!");
        }

        ResponseUtils.out(response, apiResponse);
    }

}
