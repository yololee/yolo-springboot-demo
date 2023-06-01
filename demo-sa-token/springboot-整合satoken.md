# springboot-整合sa-token

## 一、整合satoken

### 1、pom.xml

```xml
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

        <!-- Sa-Token 权限认证，在线文档：https://sa-token.cc -->
        <dependency>
            <groupId>cn.dev33</groupId>
            <artifactId>sa-token-spring-boot-starter</artifactId>
            <version>1.34.0</version>
        </dependency>
```

### 2、application.yml

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&autoReconnectForPools=true&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
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
  mapper-locations: classpath:/mapper/**Mapper.xml
  type-aliases-package: com.yolo.demosatoken.api.entity #所有Entity别名类所在包
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: ASSIGN_ID # IdType默认的全局

############## Sa-Token 配置 (文档: https://sa-token.cc) ##############
sa-token:
  # token名称 (同时也是cookie名称)
  token-name: satoken
  # token有效期，单位s 默认30天, -1代表永不过期
  timeout: 86400
  # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
  is-concurrent: true
  # 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
  is-share: false
  # token风格
  token-style: uuid
  # 是否输出操作日志
  is-log: false
```

### 3、开启权限认证

```java
/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<>();
//        list.add("101");
//        list.add("user.add");
//        list.add("user.update");
//        list.add("user.get");
        // list.add("user.delete");
//        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();

        List<UserRole> userRoles = userRoleDao.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, loginId));
        if (CollUtil.isNotEmpty(userRoles)){
            List<String> roleIds = userRoles.stream().filter(Objects::nonNull).map(UserRole::getRoleId).collect(Collectors.toList());
            List<Role> roles = roleDao.listByIds(roleIds);
            Set<String> roleNameList = roles.stream().filter(Objects::nonNull).map(Role::getName).collect(Collectors.toSet());
            list.addAll(roleNameList);
        }
        return list;
    }

}
```

### 4、开启注解鉴权

```java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册 Sa-Token 拦截器，打开注解式鉴权功能 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能 
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");    
    }
}
```

### 5、测试

```java
@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public ApiResponse login(@RequestBody @Validated LoginDTO loginDTO){
        return userService.login(loginDTO);
    }


    @PutMapping(value = "/user/add")
    @SaCheckLogin
    @SaCheckRole("admin")
    public ApiResponse add(@RequestBody @Validated AddUserDTO addUserDTO) {
        return userService.addUser(addUserDTO);
    }


    @GetMapping("/user/logout")
    @SaCheckLogin
    public ApiResponse logout(){
        StpUtil.logout();
        return ApiResponse.ofSuccess();
    }
}
```

> 登录

![image-20230601094933836](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601094933836.png)

> 注销

![image-20230601095016033](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601095016033.png)

> 注销之后然后在创建用户，需要登录之后再创建用户

![image-20230601095111158](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601095111158.png)

## 二、satoken集成redis

在分布式场景下，就需要集成redis实现登录,单机版的`Session`在分布式环境下一般不能正常工作

```xml
<!-- Sa-Token 整合 Redis （使用 jackson 序列化方式） -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-dao-redis-jackson</artifactId>
    <version>1.34.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

```yml
spring:
  redis:
    database: 0
    password: 123456 #redis密码
    sentinel:
      master: mymaster
      nodes: 116.211.105.107:26379, 116.211.105.112:26380, 116.211.105.117:26381
```

![image-20230601101955978](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601101955978.png)

> 参考文档：https://sa-token.cc/

