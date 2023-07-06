package com.yolo.demo.config.security.filter;

import com.yolo.demo.common.util.JsonUtils;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletRequest;
import com.yolo.demo.config.security.login.MyAuthenticationFailureHandler;
import com.yolo.demo.config.security.login.MyAuthenticationManager;
import com.yolo.demo.config.security.login.MyAuthenticationSuccessHandler;
import com.yolo.demo.config.security.dto.LoginBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * 自定义用户密码校验过滤器
 */
@Slf4j
@Component
public class MyAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * @param authenticationManager:             认证管理器
     * @param myAuthenticationSuccessHandler: 认证成功处理
     * @param myAuthenticationFailureHandler: 认证失败处理
     */
    public MyAuthenticationProcessingFilter(MyAuthenticationManager authenticationManager,
                                            MyAuthenticationSuccessHandler myAuthenticationSuccessHandler,
                                            MyAuthenticationFailureHandler myAuthenticationFailureHandler) {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        this.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType() == null || !request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("请求头类型不支持: " + request.getContentType());
        }

        UsernamePasswordAuthenticationToken authRequest = null;
        try {
            MultiReadHttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(request);
            // 将前端传递的数据转换成jsonBean数据格式
            LoginBody loginBody = JsonUtils.jsonToObject(wrappedRequest.getBodyJsonStrByJson(wrappedRequest), LoginBody.class);
            if (loginBody != null){
                authRequest = new UsernamePasswordAuthenticationToken(loginBody.getUserName(), loginBody.getPassword(),  new ArrayList<>());
                authRequest.setDetails(authenticationDetailsSource.buildDetails(wrappedRequest));
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}