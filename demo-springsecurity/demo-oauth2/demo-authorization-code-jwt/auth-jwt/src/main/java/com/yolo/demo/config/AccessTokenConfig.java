package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 *  配置生成token存储
 */
@Configuration
public class AccessTokenConfig {


    @Bean
    public TokenStore tokenStore() {
        // jwt -- 无状态登录，服务端不需要保存信息
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 实现将用户信息和 JWT 进行转换（将用户信息转为 jwt 字符串，或者从 jwt 字符串提取出用户信息）
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        // 原生jwt
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 自定义JWT返回信息
        JwtAccessTokenConverter converter = new MyJwt();
        // JWT 字符串生成时所需签名
        converter.setSigningKey("yolo");
        return converter;
    }

}
