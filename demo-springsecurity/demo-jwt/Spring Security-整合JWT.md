# Spring Security-整合JWT

##  一、前言

本篇文章将讲述`Spring Security 简单整合JWT 处理认证授权`

有关`JWT`不了解的可以看下官网文档：[https://jwt.io/introduction/](https://gitee.com/link?target=https%3A%2F%2Fjwt.io%2Fintroduction%2F)

## 二、实现

### 1、引入jwt依赖

```xml
        <!-- jwt依赖: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
```

### 2、修改认证处理类

```java
package com.yolo.demo.config.security.login;

import com.yolo.demo.config.Constants;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.config.security.service.UserDetailsServiceImpl;
import com.yolo.demo.domain.TSysUser;
import com.yolo.demo.mapper.TSysUserMapper;
import com.yolo.demo.util.PasswordUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义认证处理
 */
@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private TSysUserMapper tSysUserMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取前端表单中输入后返回的用户名、密码
        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        SecurityUser userInfo = (SecurityUser) userDetailsService.loadUserByUsername(userName);

        boolean isValid = PasswordUtils.isValidPassword(password, userInfo.getPassword(), userInfo.getCurrentUserInfo().getSalt());
        // 验证密码
        if (!isValid) {
            throw new BadCredentialsException("密码错误！");
        }

        // 前后端分离情况下 处理逻辑...
        // 更新登录令牌
//        String token = PasswordUtils.encodePassword(String.valueOf(System.currentTimeMillis()), userInfo.getCurrentUserInfo().getSalt());
        // 当前用户所拥有角色代码
        String roleCodes = userInfo.getRoleCodes();
        // 生成jwt访问令牌
        String jwt = Jwts.builder()
                // 用户角色
                .claim(Constants.ROLE_LOGIN, roleCodes)
                // 主题 - 存用户名
                .setSubject(authentication.getName())
                // 过期时间 - 30分钟
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                // 加密算法和密钥
                .signWith(SignatureAlgorithm.HS512, Constants.SALT)
                .compact();
        TSysUser user = tSysUserMapper.selectById(userInfo.getCurrentUserInfo().getId());
        user.setToken(jwt);
        tSysUserMapper.updateById(user);
        userInfo.getCurrentUserInfo().setToken(jwt);
        return new UsernamePasswordAuthenticationToken(userInfo, password, userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
```

### 3、认证过滤器中校验token

我们在访问每一个url请求的时候，在统一认证的地方获取jwt中我们需要的信息然后认证即可，【注： `Claims` 中存放着我们需要的信息】 例如： 我们可以将用户名、密码存放jwt中，然后在认证的时候读取到其中的用户信息，然后查询数据库认证用户，如果满足条件即成功访问，如果不满足条件即抛出异常处理

> **温馨小提示**：如果jwt令牌过期，会抛出`ExpiredJwtException`异常，我们需要拦截到，然后交给认证失败处理器中处理，然后返回给前端，这里根据个人业务实际处理即可~

```java
package com.yolo.demo.config.security.filter;



import cn.hutool.core.util.StrUtil;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletRequest;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletResponse;
import com.yolo.demo.config.Constants;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.config.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *  访问鉴权 - 每次访问接口都会经过此
 *
 */
@Slf4j
@Component
public class MyAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("请求头类型： " + request.getContentType());
        if ((request.getContentType() == null && request.getContentLength() > 0) || (request.getContentType() != null && !request.getContentType().contains(Constants.REQUEST_HEADERS_CONTENT_TYPE))) {
            filterChain.doFilter(request, response);
            return;
        }

        MultiReadHttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(request);
        MultiReadHttpServletResponse wrappedResponse = new MultiReadHttpServletResponse(response);
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            // 记录请求的消息体
            logRequestBody(wrappedRequest);
            // 前后端分离情况下，前端登录后将token储存在cookie中，每次访问接口时通过token去拿用户权限
            String jwtToken = wrappedRequest.getHeader(Constants.REQUEST_HEADER);
            log.debug("后台检查令牌:{}", jwtToken);
            if (StrUtil.isNotBlank(jwtToken)) {
                // JWT相关start ===========================================
                // 获取jwt中的信息
                Claims claims = Jwts.parser().setSigningKey(Constants.SALT).parseClaimsJws(jwtToken.replace("Bearer", "")).getBody();
                // 获取当前登录用户名
                System.out.println("获取当前登录用户名: " + claims.getSubject());
                // TODO 如需使用jwt特性在此做处理~
                // JWT相关end ===========================================

                // 检查token
                SecurityUser securityUser = userDetailsService.getUserByToken(jwtToken);
                if (securityUser == null || securityUser.getCurrentUserInfo() == null) {
                    throw new BadCredentialsException("TOKEN已过期，请重新登录！");
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                // 全局注入角色权限信息和登录用户基本信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            stopWatch.stop();
            long usedTimes = stopWatch.getTotalTimeMillis();
            // 记录响应的消息体
            logResponseBody(wrappedRequest, wrappedResponse, usedTimes);
        }

    }

    private void logRequestBody(MultiReadHttpServletRequest request) {
        if (request != null) {
            try {
                String bodyJson = request.getBodyJsonStrByJson(request);
                String url = request.getRequestURI().replace("//", "/");
                System.out.println("-------------------------------- 请求url: " + url + " --------------------------------");
                Constants.URL_MAPPING_MAP.put(url, url);
                log.info("`{}` 接收到的参数: {}",url , bodyJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void logResponseBody(MultiReadHttpServletRequest request, MultiReadHttpServletResponse response, long useTime) {
        if (response != null) {
            byte[] buf = response.getBody();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, response.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                log.info("`{}`  耗时:{}ms  返回的参数: {}", Constants.URL_MAPPING_MAP.get(request.getRequestURI()), useTime, payload);
            }
        }
    }

}
```

##  三、总结

1. 引入`jwt依赖`
2. 登录系统成功后`生成jwt令牌`返回给前端保存到`浏览器请求头`中
3. 在每一次请求访问系统url时，在统一认证过滤器中获取到请求头中jwt令牌中保存的`用户信息`然后做`认证处理`，如果满足条件成功访问，如果不满足交给认证失败处理器返回指定内容给前端