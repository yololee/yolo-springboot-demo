# Spring Security-动态url权限控制

## 一、前言

本篇文章将讲述`Spring Security 动态分配url权限，未登录权限控制，登录过后根据登录用户角色授予访问url权限`

**表结构**

![image-20230703143227803](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703143227803.png)

 **表关系简介：**

1. 用户表`t_sys_user` 关联 角色表`t_sys_role` 两者建立中间关系表`t_sys_user_role`
2. 角色表`t_sys_role` 关联 权限表`t_sys_permission` 两者建立中间关系表`t_sys_role_permission`
3. 最终体现效果为当前登录用户所具备的角色关联能访问的所有url，只要给角色分配相应的url权限即可

**表模拟数据如下：**

![image-20230703143544899](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703143544899.png)

## 二、实现

### 1、未登录访问权限控制

自定义`AdminAuthenticationEntryPoint`类实现`AuthenticationEntryPoint`类

这里是认证权限入口 -> 即在未登录的情况下访问所有接口都会拦截到此（除了放行忽略接口）

```java
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

```

### 2、自定义过滤器实现鉴权

每次访问接口都会经过此，我们可以在这里记录请求参数、响应内容，或者处理前后端分离情况下，以token换用户权限信息，token是否过期，请求头类型是否正确，防止非法请求等等

1. `logRequestBody()`方法：记录请求消息体
2. `logResponseBody()`方法：记录响应消息体

【注：请求的`HttpServletRequest流只能读一次`，下一次就不能读取了，因此这里要使用自定义的`MultiReadHttpServletRequest`工具解决流只能读一次的问题，响应同理，具体可参考文末demo源码实现】

```java
package com.yolo.demo.config.security.filter;



import cn.hutool.core.util.StrUtil;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletRequest;
import com.yolo.demo.common.util.httpservlet.MultiReadHttpServletResponse;
import com.yolo.demo.config.Constants;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.config.security.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
            String token = wrappedRequest.getHeader(Constants.REQUEST_HEADER);
            log.debug("后台检查令牌:{}", token);
            if (StrUtil.isNotBlank(token)) {
                // 检查token
                SecurityUser securityUser = userDetailsService.getUserByToken(token);
                if (securityUser == null || securityUser.getCurrentUserInfo() == null) {
                    throw new AccessDeniedException("TOKEN已过期，请重新登录！");
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

### 3、认证用户详情

`UserDetailsServiceImpl`实现`UserDetailsService`

这个在上一篇文章中也提及过，但上次未做角色权限处理，这次我们来一起加上吧

```java
package com.yolo.demo.config.security.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysUser;
import com.yolo.demo.domain.TSysUserRole;
import com.yolo.demo.mapper.TSysRoleMapper;
import com.yolo.demo.mapper.TSysUserMapper;
import com.yolo.demo.mapper.TSysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义类UserDetailsServiceImpl实现UserDetailsService类 -> 用户认证
 *
 * @author jujueaoye
 * @date 2023/06/30
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TSysUserMapper tSysUserMapper;

    @Autowired
    private TSysRoleMapper tSysRoleMapper;

    @Autowired
    private TSysUserRoleMapper tSysUserRoleMapper;

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        List<TSysUser> userList = tSysUserMapper.selectList(Wrappers.<TSysUser>lambdaQuery().eq(TSysUser::getUsername, username));
        TSysUser user;
        // 判断用户是否存在
        if (!CollectionUtils.isEmpty(userList)) {
            user = userList.get(0);
        } else {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        return new SecurityUser(user);
    }

    /***
     * 根据token获取用户权限与基本信息
     */
    public SecurityUser getUserByToken(String token) {
        TSysUser user = null;
        List<TSysUser> loginList = tSysUserMapper.selectList(Wrappers.<TSysUser>lambdaQuery().eq(TSysUser::getToken, token));
        if (!CollectionUtils.isEmpty(loginList)) {
            user = loginList.get(0);
        }

        if (ObjectUtil.isNotNull(user)) {
            return new SecurityUser(user, getUserRoles(user.getId()));
        } else {
            return null;
        }
    }

    /**
     * 根据用户id获取角色权限信息
     *
     * @param userId
     * @return
     */
    private List<TSysRole> getUserRoles(Integer userId) {
        List<TSysUserRole> userRoles = tSysUserRoleMapper.selectList(Wrappers.<TSysUserRole>lambdaQuery().eq(TSysUserRole::getUserId, userId));
        List<TSysRole> roleList = new LinkedList<>();
        for (TSysUserRole userRole : userRoles) {
            TSysRole role = tSysRoleMapper.selectById(userRole.getRoleId());
            roleList.add(role);
        }
        return roleList;
    }

}
```

自定义`SecurityUser`实现`UserDetails`

这里再说下自定义`SecurityUser `是因为Spring Security自带的 `UserDetails` （存储当前用户基本信息） 有时候可能不满足我们的需求，因此我们可以自己定义一个来扩展我们的需求`getAuthorities()`方法：即授予当前用户角色权限信息

```java
package com.yolo.demo.config.security.dto;

import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义SecurityUser类实现UserDetails类 -> 安全认证用户详情信息
 */
@Data
@Slf4j
public class SecurityUser implements UserDetails {
    /**
     * 当前登录用户
     */
    private transient TSysUser currentUserInfo;

    /**
     * 角色
     */
    private transient List<TSysRole> roleList;

    public SecurityUser() {
    }

    public SecurityUser(TSysUser user) {
        if (user != null) {
            this.currentUserInfo = user;
        }
    }

    public SecurityUser(TSysUser user, List<TSysRole> roleList) {
        if (user != null) {
            this.currentUserInfo = user;
            this.roleList = roleList;
        }
    }


    /**
     * 获取当前用户所具有的角色
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(this.roleList)) {
            for (TSysRole role : this.roleList) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getCode());
                authorities.add(authority);
            }
        }
        return authorities;
    }



    @Override
    public String getPassword() {
        return currentUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return currentUserInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### 4、获取访问该url所需要的角色权限信息

```java
package com.yolo.demo.config.security.url;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.config.Constants;
import com.yolo.demo.domain.TSysPermission;
import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysRolePermission;
import com.yolo.demo.mapper.TSysPermissionMapper;
import com.yolo.demo.mapper.TSysRoleMapper;
import com.yolo.demo.mapper.TSysRolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *  获取访问该url所需要的用户角色权限信息
 *
 */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private TSysPermissionMapper tSysPermissionMapper;
    @Autowired
    private TSysRolePermissionMapper tSysRolePermissionMapper;
    @Autowired
    private TSysRoleMapper tSysRoleMapper;

    /***
     * 返回该url所需要的用户权限信息
     *
     * @param object: 储存请求url信息
     * @return: null：标识不需要任何权限都可以访问
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 获取当前请求url
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        // TODO 忽略url请放在此处进行过滤放行
        if ("/login".equals(requestUrl) || requestUrl.contains("logout")) {
            return null;
        }

        // 数据库中所有url
        List<TSysPermission> permissionList = tSysPermissionMapper.selectList(null);
        for (TSysPermission permission : permissionList) {
            // 获取该url所对应的权限
            if (requestUrl.equals(permission.getUrl())) {
                List<TSysRolePermission> permissions = tSysRolePermissionMapper.selectList(Wrappers.<TSysRolePermission>lambdaQuery()
                        .eq(TSysRolePermission::getPermissionId,permission.getId()));
                List<String> roles = new LinkedList<>();
                if (!CollectionUtils.isEmpty(permissions)){
                    Integer roleId = permissions.get(0).getRoleId();
                    TSysRole role = tSysRoleMapper.selectById(roleId);
                    roles.add(role.getCode());
                }
                // 保存该url对应角色权限信息
                return SecurityConfig.createList(roles.toArray(new String[0]));
            }
        }
        // 如果数据中没有找到相应url资源则为非法访问，要求用户登录再进行操作
        return SecurityConfig.createList(Constants.ROLE_LOGIN);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}

```

### 5、自定义无权限处理器

在这里自定义403无权限响应内容，登录过后的权限处理
【 **注**：要和未登录时的权限处理区分开哦~ 】

```java
package com.yolo.demo.config.security.url;


import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.util.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *   认证url权限 - 登录后访问接口无权限 - 自定义403无权限响应内容
 *
 * @description : 登录过后的权限处理 【注：要和未登录时的权限处理区分开哦~】
 */
@Component
public class UrlAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        ResponseUtils.out(response, ApiResponse.ofException(403, e.getMessage()));
    }
}

```

### 6、Security 核心配置类

```java
package com.yolo.demo.config.security;

import com.yolo.demo.config.security.filter.AdminAuthenticationProcessingFilter;
import com.yolo.demo.config.security.filter.MyAuthenticationFilter;
import com.yolo.demo.config.security.login.AdminAuthenticationEntryPoint;
import com.yolo.demo.config.security.url.UrlAccessDecisionManager;
import com.yolo.demo.config.security.url.UrlAccessDeniedHandler;
import com.yolo.demo.config.security.url.UrlFilterInvocationSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 访问鉴权 - 认证token、签名...
     */
    @Autowired
    private MyAuthenticationFilter myAuthenticationFilter;

    /**
     * 访问权限认证异常处理
     */
    @Autowired
    private AdminAuthenticationEntryPoint adminAuthenticationEntryPoint;

    /**
     * 用户密码校验过滤器
     */
    @Autowired
    private AdminAuthenticationProcessingFilter adminAuthenticationProcessingFilter;

    // 上面是登录认证相关  下面为url权限相关 - ========================================================================================

    /**
     * 获取访问url所需要的角色信息
     */
    @Autowired
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    /**
     * 认证权限处理 - 将上面所获得角色权限与当前登录用户的角色做对比，如果包含其中一个角色即可正常访问
     */
    @Autowired
    private UrlAccessDecisionManager urlAccessDecisionManager;

    /**
     * 自定义访问无权限接口时403响应内容
     */
    @Autowired
    private UrlAccessDeniedHandler urlAccessDeniedHandler;


    /**
     * 权限配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.antMatcher("/**").authorizeRequests();

        // 禁用CSRF 开启跨域
        http.csrf().disable().cors();

        // 未登录认证异常
        http.exceptionHandling().authenticationEntryPoint(adminAuthenticationEntryPoint);
        // 登录过后访问无权限的接口时自定义403响应内容
        http.exceptionHandling().accessDeniedHandler(urlAccessDeniedHandler);

        // url权限认证处理
        registry.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(urlAccessDecisionManager);
                return o;
            }
        });

        // 不创建会话 - 即通过前端传token到后台过滤器中验证是否存在访问权限
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 标识访问 `/home` 这个接口，需要具备`ADMIN`角色
//        registry.antMatchers("/home").hasRole("ADMIN");
        // 标识只能在 服务器本地ip[127.0.0.1或localhost] 访问 `/home` 这个接口，其他ip地址无法访问
        registry.antMatchers("/home").hasIpAddress("127.0.0.1");
        // 允许匿名的url - 可理解为放行接口 - 多个接口使用,分割
        registry.antMatchers("/login", "/index").permitAll();
//        registry.antMatchers("/**").access("hasAuthority('admin')");
        // OPTIONS(选项)：查找适用于一个特定网址资源的通讯选择。 在不需执行具体的涉及数据传输的动作情况下， 允许客户端来确定与资源相关的选项以及 / 或者要求， 或是一个服务器的性能
        registry.antMatchers(HttpMethod.OPTIONS, "/**").denyAll();
        // 自动登录 - cookie储存方式
        registry.and().rememberMe();
        // 其余所有请求都需要认证
        registry.anyRequest().authenticated();
        // 防止iframe 造成跨域
        registry.and().headers().frameOptions().disable();

        // 自定义过滤器在登录时认证用户名、密码
        http.addFilterAt(adminAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(myAuthenticationFilter, BasicAuthenticationFilter.class);
    }

    /**
     * 忽略拦截url或静态资源文件夹 - web.ignoring(): 会直接过滤该url - 将不会经过Spring Security过滤器链
     * http.permitAll(): 不会绕开springsecurity验证，相当于是允许该路径通过
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.GET,
                "/favicon.ico",
                "/*.html",
                "/**/*.css",
                "/**/*.js");
    }

}
```

## 三、编写测试代码

```java
package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
public class IndexController {

    @GetMapping("/")
    public ModelAndView showHome() {
        return new ModelAndView("home.html");
    }

    @GetMapping("/index")
    public String index() {
        return "Hello World ~";
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    @GetMapping("/home")
    public String home() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("登陆人：" + name);
        return "Hello~ " + name;
    }

    @GetMapping(value ="/admin")
    // 访问路径`/admin` 具有`ADMIN`角色权限   【这种是写死方式】
//    @PreAuthorize("hasPermission('/admin','ADMIN')")
    public String admin() {
        return "Hello~ 管理员";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello~ 测试权限访问接口";
    }
    
}
```

## 四、测试

1、未登录时

![image-20230703145429177](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703145429177.png)

2、登录过后如果有权限则正常访问

因为这里我们代码里面是模拟的前后端分离需要携带请求头，然后把token传进去

![image-20230703145908887](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703145908887.png)

3、登录过后，没有权限

这里我们可以修改数据库角色权限关联表t_sys_role_permission来进行测试哦 ~

Security 动态url权限也就是依赖这张表来判断的，只要修改这张表分配角色对应url权限资源，用户访问url时就会动态的去判断，无需做其他处理，如果是将权限信息放在了缓存中，修改表数据时及时更新缓存即可！

![](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703150316123.png)

![image-20230703150408931](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703150408931.png)

4、登录过后，访问数据库中没有配置的url 并且 在Security中没有忽略拦截的url时

![image-20230703151356952](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230703151356952.png)

