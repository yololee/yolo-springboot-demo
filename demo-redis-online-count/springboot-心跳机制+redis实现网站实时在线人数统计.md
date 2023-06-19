# springboot-心跳机制+redis实现网站实时在线人数统计

## 一、前言

市面上主流的做法：

- 创建一个session监听器，在用户登录时即创建一个session，监听器记录下来并且把count加一
- 用户点击注销时把session给remove掉，count减一

弊端：

- 当用户关闭浏览器时并不会触发session监听，当下一次登录时仍然会让count加一
- 或者在session过期时，session监听并不能做一个实时的响应去将在线数减一
- 当用户在次登陆，由于cookie中含有的session_id不同而导致session监听器记录下session创建，而使count加一
- 对服务器性能影响较大，用户每次访问网站时，服务端都会创建一个session,并将该session与用户关联起来，这样会增加服务器的负担，特别是在高并发的时候，导致服务器压力过大
- 容易被恶意攻击，攻击者不断发送ddox请求大量创建肉鸡用户，从而大量占据服务器资源，从而崩坏
- 分布式环境下不好操作

**使用用户登录凭证:token机制+心跳机制实现**

![](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619210436752.png)

**实现思路：**

<font color ='red'>根据时序图的这套方案，用户如果60s内没有任何操作（不调用接口去传递token）则判定该用户为下线状态，当用户重新登陆或者再次操作网站则判定为在线状态，对用户的token进行续期。这其实是心跳机制思想的一种实现，类似于Redis集群中的哨兵对Master主观下线的过程：每10s对Master发送一个心跳包，10s内没有响应则说明Master已经下线了。这里采用的是60s作为一个生存值，如果60s内该用户没有在此页面（如果在此页面，前端会间隔10s发送一次心跳包对Token进行续期+60s过期时间）上执行任何操作，也就不会携带Token发送请求到后端接口中，那么就无法给map中的token过期时间续期，所以该用户就处于过期状态</font>

## 二、整合

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--MySQL 5.1.47-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--druid 数据库连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.24</version>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>

        <!--redis依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!-- 对象池，使用redis时必须引入 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!--fastjson依赖-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.33</version>
        </dependency>
        <!--jwt依赖-->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.0</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>


        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

### 2、配置文件

```yml
server:
  port: 8080
spring:
  profiles:
    active:
      - mysql
      - redis
```

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/test1?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
    username: root
    password: root
    druid:
      initialSize: 5
      minIdle: 5
      maxActive: 20
      # 配置获取连接等待超时的时间
      maxWait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      timeBetweenEvictionRunsMillis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      minEvictableIdleTimeMillis: 300000
      validationQuery: select 'x'
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      # 打开PSCache，并且指定每个连接上PSCache的大小
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      filters: stat,wall,slf4j
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000

mybatis-plus:
  #mybatis配置文件
  #config-location: classpath:mybatis-config.xml
  # mapper映射位置
  mapper-locations: classpath:/mapper/**Mapper.xml
  #所有domain别名类所在包
  type-aliases-package: com.yolo.demo.domain
  configuration:
    # 用来打印sql日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    #类属性与表字段的驼峰映射，mybatiplus默认true开启，mybatis需要手动配置，且config-location和configuration不能同时出现
    map-underscore-to-camel-case: true
  #全局配置
  global-config:
    #数据库配置
    db-config:
      #主键策略
      id-type: ASSIGN_ID  # IdType默认的全局
      #表名前缀为tb_，表名为前缀拼接类名（小写）
      #      table-prefix: tb_
      logic-delete-field: removed # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: -1 # 逻辑已删除值(默认为 -1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

```yml
spring:
  redis:
    host: 127.0.0.1
    # 连接超时时间（记得添加单位，Duration）
    timeout: 10000ms
    # Redis默认情况下有16个分片，这里配置具体使用的分片
    database: 0
    lettuce:
      pool:
        # 连接池最大连接数（使用负值表示没有限制） 默认 8
        max-active: 8
        # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-wait: -1ms
        # 连接池中的最大空闲连接 默认 8
        max-idle: 8
        # 连接池中的最小空闲连接 默认 0
        min-idle: 0
```

### 3、redis序列化配置

```java
package com.yolo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key 序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value 序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // hash 类型 key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // hash 类型 value序列化方式
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 让设置生效
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

### 4、统计用户在线人数

```java
package com.yolo.demo.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineCounter {

    /**
     * 每次打开此类是该属性只初始化一次
     */
    private static final Map<String,Object> COUNT_MAP = new ConcurrentHashMap<>();


    /**
     * 当一个用户登录时，就往map中构建一个k-v键值对
     * k- 用户名，v 当前时间+过期时间间隔，这里以60s为例子
     * 如果用户在过期时间间隔内频繁对网站进行操作，那摩对应
     * 她的登录凭证token的有效期也会一直续期，因此这里使用用户名作为k可以覆盖之前
     * 用户登录的旧值，从而不会出现重复统计的情况
     */
    public void insertToken(String userName){
        long currentTime = System.currentTimeMillis();
        COUNT_MAP.put(userName,currentTime+60*1000);
    }

    /**
     * 当用户注销登录时，将移除map中对应的键值对
     * 避免当用户下线时，该计数器还错误的将该用户当作
     * 在线用户进行统计
     * @param userName
     */
    public void deleteToken(String userName){
        COUNT_MAP.remove(userName);
    }

    /**
     * 统计用户在线的人数
     * @return
     */
    public Integer getOnlineCount(){
        int onlineCount = 0;
        Set<String> nameList = COUNT_MAP.keySet();
        long currentTime = System.currentTimeMillis();
        for (String name : nameList) {
            Long value = (Long) COUNT_MAP.get(name);
            if (value > currentTime){
                // 说明该用户登录的令牌还没有过期
                onlineCount++;
            }
        }
        return onlineCount;
    }
}

```

### 5、工具类

> 线程隔离工具类

```java
package com.yolo.demo.utils;


import com.yolo.demo.domain.User;
import org.springframework.stereotype.Component;

/**
 * 线程隔离，用于替代session
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }


}


```

> jwt工具类

```java
package com.yolo.demo.utils;

import cn.hutool.core.lang.UUID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    //有效期为
    public static final Long JWT_TTL = 60 * 60 *1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "sangeng";

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("sg")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjYWM2ZDVhZi1mNjVlLTQ0MDAtYjcxMi0zYWEwOGIyOTIwYjQiLCJzdWIiOiJzZyIsImlzcyI6InNnIiwiaWF0IjoxNjM4MTA2NzEyLCJleHAiOjE2MzgxMTAzMTJ9.JVsSbkP94wuczb4QryQbAke3ysBDIL5ou8fWsbt_ebg";
        Claims claims = parseJWT(token);
        System.out.println(claims);
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

}


```

> 有时候我们需要在响应流中设置返回数据，因此有如下工具类

```java
	package com.yolo.demo.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebUtils {
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
```

### 6、配置拦截器

我们这里可以使用springboot的拦截器来拦截需要登录后才能操作的接口，操作这些接口就代表的当前用户属于登录状态，因此需要给用户的登录凭证也就是token续期，对应的往map中添加用户的过期时间来进行覆盖之前的，这样就不会出现同一个用户出现重复统计的情况

```java
package com.yolo.demo.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.domain.User;
import com.yolo.demo.utils.HostHolder;
import com.yolo.demo.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private OnlineCounter onlineCounter;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token)){
            ApiResponse apiResponse = ApiResponse.of(400, "未携带请求头信息，不合法", null);
            String jsonStr = JSONUtil.toJsonStr(apiResponse);
            WebUtils.renderString(response,jsonStr);
            return false;
        }
        User user =(User) redisTemplate.opsForValue().get(token);
        if (ObjectUtil.isNull(user)){
            ApiResponse apiResponse = ApiResponse.of(400, "token过期，请重新登录", null);
            String jsonStr = JSONUtil.toJsonStr(apiResponse);
            WebUtils.renderString(response,jsonStr);
            return false;
        }

        // 当请求执行到此处，说明当前token是有效的,对token续期
        redisTemplate.opsForValue().set(token,user,60, TimeUnit.SECONDS);
        // 在本次请求中持有当前用户，方便业务使用
        hostHolder.setUser(user);
        // 覆盖之前的map统计时间，使用最新的token有效期时长
        onlineCounter.insertToken(user.getName());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         // 释放前挡用户，防止内存泄露
         hostHolder.clear();
    }

}
```

> 使拦截器生效

```java
package com.yolo.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 配置拦截哪些请求
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/login","/online"); // 不拦截这些资源
    }
}
```

### 7、实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer id;
    private String name;
    private String password;
    private Integer age;
}


@Data
public class LoginParam {
    private String name;
    private String password;
}
```

### 8、接口层

```java
@RestController
public class HelloController {

    @Autowired
    private UserService userService;


    /**
     * 该接口需要登录后才能操作
     * @return
     */
    @RequestMapping("/user/list")
    public ApiResponse hello(){
        return userService.selectUserList();
    }


    /**
     * 登录
     * @param loginParam
     * @return
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginParam loginParam){
        return userService.login(loginParam);
    }


    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request){
        return userService.logout(request);
    }


    /**
     * 获取当前在线人数
     * 这个就相当于一个心跳检查机制
     * 前端每间隔一定时间就请求一下该接口达到在线人数
     * @return
     */
    @PostMapping("/online")
    public ApiResponse getOnLineCount(){
        return userService.getOnLineCount();
    }
}
```

### 9、业务层

```java
package com.yolo.demo.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.config.OnlineCounter;
import com.yolo.demo.domain.User;
import com.yolo.demo.dto.LoginParam;
import com.yolo.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private OnlineCounter onlineCounter;

    @Override
    public ApiResponse selectUserList() {
        return ApiResponse.ofSuccess(list());
    }

    @Override
    public ApiResponse login(LoginParam loginParam) {
        String name = loginParam.getName();

        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getName, name));
        if (ObjectUtil.isNull(user)){
            throw new RuntimeException("用户名或者密码不正确");
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        log.info("当前账号对应的token是: {}",token);
        redisTemplate.opsForValue().set(token,user,60, TimeUnit.SECONDS);
        // 往map中添加一条用户记录
        onlineCounter.insertToken(name);
        return ApiResponse.ofSuccess();
    }

    @Override
    public ApiResponse logout(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        User user = (User) redisTemplate.opsForValue().get(authorization);
        redisTemplate.delete(authorization);
        if (ObjectUtil.isNotNull(user)){
            onlineCounter.deleteToken(user.getName());
        }

        return ApiResponse.ofSuccess();
    }

    @Override
    public ApiResponse getOnLineCount() {
        return ApiResponse.ofSuccess(onlineCounter.getOnlineCount());
    }
}
```

## 三、测试

未登录时去操作需要登录的接口或者token过期了

![image-20230619215701511](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619215701511.png)

这个时候网站的在线人数：

![image-20230619215733110](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619215733110.png)

登录后：

![image-20230619215800049](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619215800049.png)

这时候再去请求需要登录才能访问的接口

![image-20230619215941396](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619215941396.png)

可以看到成功访问了，并且该用户的token会一直续期

获取当前在线人数：

![image-20230619220006184](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230619220006184.png)