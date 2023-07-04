package com.yolo.demo.config.security.filter;

import com.yolo.demo.common.util.JsonUtils;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletRequest;
import com.yolo.demo.config.security.login.AdminAuthenticationFailureHandler;
import com.yolo.demo.config.security.login.AdminAuthenticationSuccessHandler;
import com.yolo.demo.config.security.login.CusAuthenticationManager;
import com.yolo.demo.domain.TSysUser;
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

/**
 * 自定义用户密码校验过滤器
 */
@Slf4j
@Component
public class AdminAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * @param authenticationManager:             认证管理器
     * @param adminAuthenticationSuccessHandler: 认证成功处理
     * @param adminAuthenticationFailureHandler: 认证失败处理
     */
    public AdminAuthenticationProcessingFilter(CusAuthenticationManager authenticationManager,
                                               AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler,
                                               AdminAuthenticationFailureHandler adminAuthenticationFailureHandler) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationSuccessHandler(adminAuthenticationSuccessHandler);
        this.setAuthenticationFailureHandler(adminAuthenticationFailureHandler);
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
            TSysUser user = JsonUtils.jsonToObject(wrappedRequest.getBodyJsonStrByJson(wrappedRequest), TSysUser.class);
            if (user != null){
                authRequest = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), null);
                authRequest.setDetails(authenticationDetailsSource.buildDetails(wrappedRequest));
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}