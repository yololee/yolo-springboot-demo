# Spring Security-整合OAuth2之授权码模式

## 一、介绍OAuth2

OAuth 是一个开放标准，允许用户授权第三方应用访问他们在某网站上存储的私密资源（ex:用户昵称、头像等），在这个过程中无需将用户名和密码提供给第三方应用。

**应用场景**

第三方应用授权登录，例如：qq、微信授权登录

## 二、授权码模式

一共有四种授权模式，本篇文章讲解授权码模式（authorization code）

> 常用模式，主流第三方验证

1. 第三方应用引导用户跳转到授权服务器的授权页面，授权后，授权服务器生成认证码code，然后携带code重定向返回
2. 第三方应用使用认证码code和自身应用凭证(app_id和app_secret)到授权服务器换取访问令牌(access_token)和更新令牌(refresh_token)
3. 第三方应用使用访问令牌(access_token)去资源服务器获取资源信息(ex:用户昵称，头像等)

![image-20230707114821143](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230707114821143.png)

## 三、代码实现

![image-20230707114935811](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230707114935811.png)

| 项目   | 端口  | 备注       |
| ------ | ----- | ---------- |
| auth   | 10010 | 授权服务器 |
| client | 10020 | 第三方应用 |
| user   | 10030 | 资源服务器 |

### 授权服务器

#### Security 核心配置类

```java
package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 *  Security 核心配置类
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户 -- 校验用户
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                // admin
                .withUser("admin")
                .password(new BCryptPasswordEncoder().encode("123456"))
                .roles("admin")
                // test
                .and()
                .withUser("test")
                .password(new BCryptPasswordEncoder().encode("123456"))
                .roles("test");
    }

    /**
     * 权限配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用CSRF 表单登录
        http.csrf().disable().formLogin();
    }

}
```

#### 授权服务器配置

```java
package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;


/**
 *  授权服务器配置
 */
@Configuration
// 开启授权服务器的自动化配置
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private TokenStore tokenStore;

    @Resource
    private ClientDetailsService clientDetailsService;

    /**
     * 配置令牌端点的安全约束
     * 即这个端点谁能访问，谁不能访问
     * checkTokenAccess: 指一个 Token 校验的端点，这个端点我们设置为可以直接访问（当资源服务器收到 Token 之后，需要去校验 Token 的合法性，就会访问这个端点）
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 配置客户端的详细信息 -- 校验客户端
     *
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // id
                .withClient("yolo_app_id")
                // secret
                .secret(new BCryptPasswordEncoder().encode("yolo_app_secret"))
                // 资源id
                .resourceIds("res1")
                // 授权类型 -- 授权码模式
                .authorizedGrantTypes("authorization_code", "refresh_token")
                // 授权范围
                .scopes("all")
                // 重定向uri
                .redirectUris("http://127.0.0.1:10020/index.html");
    }

    /**
     * 配置令牌的访问端点和令牌服务
     * 授权码和令牌有什么区别？授权码是用来获取令牌的，使用一次就失效，令牌则是用来获取资源的
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 配置授权码的存储
        endpoints.authorizationCodeServices(this.authorizationCodeServices())
                // 配置令牌的存储
                .tokenServices(this.tokenServices());
    }

    /**
     * 配置授权码的存储
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 内存
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 配置 Token 的一些基本信息
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(this.clientDetailsService);
        // 是否支持刷新
        services.setSupportRefreshToken(true);
        // 存储位置
        services.setTokenStore(this.tokenStore);
        // 有效期
        services.setAccessTokenValiditySeconds(60 * 60);
        // 刷新token的有效期 -- 当token块过期的时候，需要获取一个新的token，在获取新token的时候，需要一个凭证信息，这个凭证信息不是旧的 Token，而是另外一个 refresh_token，这个 refresh_token 也是有有效期的。
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        return services;
    }

}

```

#### 配置生成token存储

```java
package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 *  配置生成token存储
 */
@Configuration
public class AccessTokenConfig {
    @Bean
    TokenStore tokenStore() {
        // 内存
        return new InMemoryTokenStore();
    }
}
```

### 第三方应用

#### index.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>APP</title>
</head>
<body>
Hello World! <br/>

<!--
    点击超链接实现第三方登录
    client_id 客户端 ID，根据我们在授权服务器中的实际配置填写。
    response_type 表示响应类型，这里是 code 表示响应一个授权码。
    redirect_uri 表示授权成功后的重定向地址，这里表示回到第三方应用的首页。
    scope 表示授权范围。
-->
<a href="http://127.0.0.1:10010/oauth/authorize?client_id=zq_app_id&response_type=code&scope=all&redirect_uri=http://127.0.0.1:10020/index.html">第三方登录</a>

<h1 th:text="${msg}"></h1>

</body>
</html>
```

#### 测试api

```java
package com.yolo.demo.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 测试api
 */
@Slf4j
@Controller
@RequestMapping("")
public class TestController {

    @GetMapping("/index.html")
    public String hello(String code, Model model) {
        if (StringUtils.isNotBlank(code)) {
            RestTemplate restTemplate = new RestTemplate();
            /**
             * 如果 code 不为 null，标识是通过授权服务器重定向到这个地址来的
             * 根据拿到的 code 去获取 Token
             */
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("code", code);
            map.add("client_id", "yolo_app_id");
            map.add("client_secret", "yolo_app_secret");
            map.add("redirect_uri", "http://127.0.0.1:10020/index.html");
            map.add("grant_type", "authorization_code");
            // 刷新令牌
//            map.add("grant_type", "refresh_token");
//            map.add("refresh_token", "07a12e6d-73f4-43c0-9533-dd28d17449a5");
            Map<String, String> authResponseMap = restTemplate.postForObject("http://127.0.0.1:10010/oauth/token", map, Map.class);
            log.info("authResponse: {}", JSON.toJSONString(authResponseMap));
            String accessToken = authResponseMap.get("access_token");


            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> entity = restTemplate.exchange("http://127.0.0.1:10030/admin/hello", HttpMethod.GET, httpEntity, String.class);
            model.addAttribute("msg", entity.getBody());
        }
        return "index";
    }

}

```

### 资源服务器

#### 资源服务器配置

```java
package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 *  资源服务器配置
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 配置一个 RemoteTokenServices 的实例，因为资源服务器和授权服务器是分开的；
     * 如果资源服务器和授权服务器是放在一起的，就不需要配置 RemoteTokenServices 了。
     * <p>
     * 当用户来资源服务器请求资源时，会携带上一个 access_token，通过这里的配置，就能够校验出 token 是否正确等。
     */
    @Bean
    RemoteTokenServices tokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        // access_token 的校验地址
        services.setCheckTokenEndpointUrl("http://127.0.0.1:10010/oauth/check_token");
        services.setClientId("yolo_app_id");
        services.setClientSecret("yolo_app_secret");
        return services;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenServices(this.tokenServices());
    }

    /**
     * 配置资源的拦截规则
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .anyRequest().authenticated();
    }
}

```

#### 测试api

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }
}
```

