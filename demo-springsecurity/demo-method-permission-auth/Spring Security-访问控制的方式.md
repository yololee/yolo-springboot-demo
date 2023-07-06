# Spring Security-访问控制的方式

## 授权管理

### anyReques

看名字就知道，这个表示所有的请求。但是这个 `anyRequest` 有个坑点，不能配置 `anyRequest` 在 `antMatchers` 前面，一般这个 `anyRequest` 是放在放行规则的最后面

```java
http
        // 验证策略
        .authorizeRequests()
        // 放行登录
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        .antMatchers(HttpMethod.POST, "/error").permitAll()
        .antMatchers(HttpMethod.POST, "/doLogout").permitAll()
        .anyRequest().authenticated()
```

### antMatchers

这个 `antMatchers` 用于匹配请求，可以使用 `ant` 语法，匹配路径可以使用 `*` 来匹配

```java
http
        // 验证策略
        .authorizeRequests()
        // 可以用来放行静态资源
        .antMatchers("/css/**","/js/**","/images/**").permitAll()
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        // 也可以放行所有目录下的 png 图片
        .antMatchers("/**/*.png").permitAll()
        .anyRequest().authenticated()
```

Copy

上面的匹配也可以只匹配特定类型（不写默认匹配所有类型的请求），使用 `HttpMethod` 这个枚举

```java
public enum HttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
    ...
```

Copy

### regexMatcher

除了向上面那个使用 `ant` 语法进行匹配的 `antMatchers` 也有直接使用正则表达式进行匹配的 `regexMatchers`

```java
http
        // 验证策略
        .authorizeRequests()
        // 放行所有目录下的 png 图片
        .regexMatchers(".+[.]png").permitAll()
        .anyRequest().authenticated()
```

### 内置控制访问方法

| 表达式               | 备注                                     |
| -------------------- | ---------------------------------------- |
| hasRole              | 用户具备某个角色即可访问资源             |
| hasAnyRole           | 用户具备多个角色中的任意一个即可访问资源 |
| hasAuthority         | 类似于 hasRole                           |
| hasAnyAuthority      | 类似于 hasAnyRole                        |
| permitAll            | 统统允许访问                             |
| denyAll              | 统统拒绝访问                             |
| isAnonymous          | 判断是否匿名用户                         |
| isAuthenticated      | 判断是否认证成功                         |
| isRememberMe         | 判断是否通过记住我登录的                 |
| isFullyAuthenticated | 判断是否用户名/密码登录的                |
| principle            | 当前用户                                 |
| authentication       | 从 SecurityContext 中提取出来的用户对象  |

### 基于权限访问

例如配置一个请求只有 admin 才能访问

```java
http
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        // 注意这个 hasAuthority 对大小写有严格要求
        .antMatchers("/hello").hasAuthority("admin")
        // 或者使用这个 hasAnyAuthority，表示 "admin","temp" 任意一个都可以
        .antMatchers("/hello").hasAnyAuthority("admin","temp")
        .anyRequest().authenticated();
```

### 基于角色访问

先修改下 `UserDetailServiceImpl` 的返回值，使其添加角色

```java
// 在这个 commaSeparatedStringToAuthorityList 后面直接加上以 ROLE 开头的角色（必须是这个，且是大写）
return new User(username, password, AuthorityUtils
                // 例如这里就创建了 admin 和 abc 这两个角色
                .commaSeparatedStringToAuthorityList("admin,normal,ROLE_admin,ROLE_abc"));
```

然后就可以基于角色访问了

```java
http
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        // 这个 hasRole 也区分大小写
        // 这里就不用写 ROLE 了，这里需要 abc 这个角色
        .antMatchers("/hello").hasRole("abc")
        .anyRequest().authenticated();
```

### 基于 IP 地址

例如服务器内部访问时这个基于 IP 地址就很有用了，防止外部调用某个服务（Spring Cloud）

```java
// 可以在 Request 获取远程地址
log.info(request.getRemoteAddr());
```

例如这里只允许 `127.0.0.1` 访问

```java
http
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        // 注意这个 hasRole 对大小写有严格要求
        .antMatchers("/hello").hasIpAddress("127.0.0.1")
        .anyRequest().authenticated();
```

### GrantedAuthority

参考资料 [【详解】Spring Security的GrantedAuthority（已授予的权限）](https://blog.csdn.net/qq_22078107/article/details/106654924)

之前编写 `UserDetails` 时，里面是有一个集合专门来存储 `GrantedAuthority` 对象的（`UserDeitails` 接口里面有一个`getAuthorities()` 方法。这个方法将返回此用户的所拥有的权限。这个集合将用于用户的访问控制，也就是 `Authorization`）

```java
// 如下，添加两个权限
return new User(username, password, AuthorityUtils
        .commaSeparatedStringToAuthorityList("admin,normal"));
```

所谓权限，就是一个字符串。一般不会重复。 而所谓权限检查，就是查看用户权限列表中是否含有匹配的字符串。

```java
public interface GrantedAuthority extends Serializable {
    String getAuthority();
}
```

在 Security 提供的 `UserDetailsService` 默认实现中，角色和权限都存储在 `authorities` 表中

```java
Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
```

`GrantedAuthority` 接口的默认实现类 `SimpleGrantedAuthority` 其实就只是用来比对字符串是否匹配

```java
public final class SimpleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 500L;
    private final String role;
 
    public SimpleGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }
 
    public String getAuthority() {
        return this.role;
    }
 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof SimpleGrantedAuthority ? this.role.equals(((SimpleGrantedAuthority)obj).role) : false;
        }
    }
 
    public int hashCode() {
        return this.role.hashCode();
    }
 
    public String toString() {
        return this.role;
    }
}
```

### access 方法

参考文档 [常见的内置表达式](https://www.docs4dev.com/docs/zh/spring-security/4.2.10.RELEASE/reference/el-access.html) 参考资料 [Spring Security 基于表达式的权限控制](https://my.oschina.net/liuyuantao/blog/1924776)

实际上，内置的访问控制方法都是基于这个 `access()` 方法进行的封装，这个 `access()` 传入一个 `SpringEL` 表达式来控制授权（什么是 `SpringEl` 参考 Spring03 笔记）

```java
hasRole("abc")
// 等价
access("hasRole('abc')")

// 除了像上面那样调用一个内置的方法，也可以使用 SpEL 来编写逻辑语句
config.antMatchers("/person/*").access("hasRole('ADMIN') or hasRole('USER')")
                // 在Web安全表达式中引用bean
                .antMatchers("/person/{id}").access("@myServiceImpl.checkUserId(authentication,#id)")
                .anyRequest().access("@myServiceImpl.hasPermission(request,authentication)");
```

如下可见，实际上 `permitAll`、`hasAuthority` 和 `hasAnyRole` 方法都是基于这个 `access` 方法的二次封装

```java
public ExpressionInterceptUrlRegistry permitAll() {
    return access(permitAll);
}

public ExpressionInterceptUrlRegistry hasAuthority(String authority) {
    return access(ExpressionUrlAuthorizationConfigurer.hasAuthority(authority));
}

public ExpressionInterceptUrlRegistry hasAnyRole(String... roles) {
    return access(ExpressionUrlAuthorizationConfigurer.hasAnyRole(roles));
}
```

## 内置的 EL 表达式

内置的访问控制方法

| 表达式                         | 描述                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| hasRole([role])                | 用户拥有制定的角色时返回true （Spring security默认会带有ROLE*前缀）,去除参考Remove the ROLE* |
| hasAnyRole([role1,role2])      | 用户拥有任意一个制定的角色时返回true                         |
| hasAuthority([authority])      | 等同于hasRole,但不会带有ROLE_前缀                            |
| hasAnyAuthority([auth1,auth2]) | 等同于hasAnyRole                                             |
| permitAll                      | 永远返回true                                                 |
| denyAll                        | 永远返回false                                                |
| anonymous                      | 当前用户是anonymous时返回true                                |
| rememberMe                     | 当前勇士是rememberMe用户返回true                             |
| authentication                 | 当前登录用户的authentication对象                             |
| fullAuthenticated              | 当前用户既不是anonymous也不是rememberMe用户时返回true        |
| hasIpAddress('192.168.1.0/24') | 请求发送的IP匹配时返回true                                   |

### 自定义 access

先创建一个接口

```java
public interface MyService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
```

然后创建实现类

```java
@Service
@Slf4j
public class MyServiceImpl implements MyService {
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        // 获取主体
        Object principal = authentication.getPrincipal();
        log.info(request.getRequestURI());
        // 判断主体是否属于 UserDetails
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // 获取权限列表
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            // 判断请求的 URI 是否在权限里（这个还是需要在 UserDetails 里的返回值 User 加上需要访问的路径）
            return authorities.contains(new SimpleGrantedAuthority(request.getRequestURI()));
        }
        return false;
    }
}
```

在 `UserDetails` 里的返回值加上这个访问权限

```java
return new User(username, password, AuthorityUtils
// 这里的 /hello 不要想太多，不加 ROLE 的单纯就只是一个用来匹配的字符串
                .commaSeparatedStringToAuthorityList("admin,normal,ROLE_admin,ROLE_abc,/hello"));
```

最后在 `access` 里调用这个 Bean

```java
http
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/doLogin").permitAll()
        // 使用自定义的 access 方法
        .anyRequest().access("@myServiceImpl.hasPermission(request,authentication)");
```

## 基于注解的访问控制

参考资料 [Spring Security 注解](https://www.cnblogs.com/LoveShare/p/12666680.html)

首先在启动类上加 `@EnableGlobalMethodSecurity` 注解开启 Security 注解支持

因为默认 `@EnableGlobalMethodSecurity` 的注解都是单独设置的且全部为 `false`，所以需要手动开启

```java
@SpringBootApplication
@ServletComponentScan
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

}
```

常用的注解（下面的三个注解都可以写在类或方法上）

`@Secured` 用来控制一个方法是否能被访问（只验证 Authority，如果要验证 ROLE 需要手动加上 `ROLE_`）

`@PreAuthorize` 更精确的控制是否能被访问

`@PostAuthorize` 在方法执行后再进行权限验证，在方法调用完成后检查权限决定是否抛出 `AccessDeniedException` 异常

> 注：这些注解一般写在 Controller 上（也可以写在 Service 接口或者方法上，但是一般都是 Controller 上，控制 URL 是否允许被访问）

### @Secured[]

这个用来验证角色的

```java
@GetMapping("/helloUser")
@Secured({"ROLE_normal","ROLE_admin"})
public String helloUser() {
    return "hello,user";
}
```

拥有 `normal` 或者 `admin` 角色的用户都可以方法 `helloUser()` 方法。另外需要注意的是这里匹配的字符串需要添加前缀 `ROLE_`。（一定得添加才能用，如果想单独使用 Authorize 来鉴权，一般用下面的 `@PreAuthorize` 注解）

### @PreAuthorize

其实这个就是上面的 `access` 方法

```java
@GetMapping("/helloUser01")
@PreAuthorize("hasAnyRole('normal','admin')")
public String helloUser01() {
    return "hello,user01";
}


@GetMapping("/helloUser02")
@PreAuthorize("hasRole('normal') AND hasRole('admin')") 
public String helloUser02() {
    return "hello,user02";
}
```

### @PostAuthorize

```java
@GetMapping("/helloUser")
@PostAuthorize(" returnObject!=null &&  returnObject.username == authentication.name")
public User helloUser() {
        Object pricipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user;
        if("anonymousUser".equals(pricipal)) {
            user = null;
        }else {
            user = (User) pricipal;
        }
        return user;
}
```