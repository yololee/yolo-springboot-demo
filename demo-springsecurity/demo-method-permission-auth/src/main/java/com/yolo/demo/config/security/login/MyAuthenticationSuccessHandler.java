package com.yolo.demo.config.security.login;


import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.config.Constants;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.domain.User;
import com.yolo.demo.util.ResponseUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 *  认证成功处理
 */
@Component
@Slf4j
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
        // 生成jwt 放入 Header
        SecurityUser securityUser = ((SecurityUser) auth.getPrincipal());

        System.out.println("认证成功");

        // 生成jwt访问令牌
        String jwtToken = Jwts.builder()
                // 用户角色
                .claim(Constants.PERMISSION_KEY, securityUser.getAuthorities())
                // 主题 - 存用户名
                .setSubject(auth.getName())
                // 过期时间 - 30分钟
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                // 加密算法和密钥
                .signWith(SignatureAlgorithm.HS512, Constants.SALT)
                .compact();

        response.addHeader(Constants.REQUEST_HEADER, jwtToken);
        // 响应
        ResponseUtils.out(response, ApiResponse.ofSuccess(securityUser));
    }

}
