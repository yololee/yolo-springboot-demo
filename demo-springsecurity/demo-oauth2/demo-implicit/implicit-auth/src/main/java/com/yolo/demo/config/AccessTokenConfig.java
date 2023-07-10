package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * 配置生成token存储
 */
@Configuration
public class AccessTokenConfig {
    @Bean
    TokenStore tokenStore() {
        // 内存
        return new InMemoryTokenStore();
    }
}
