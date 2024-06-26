# yolo-springboot-demo

## 介绍
spring boot demo 是一个用来深度学习并实战 spring boot 的项目


## 开发环境
- JDK 1.8 +
- Maven 3.5 +
- IntelliJ IDEA ULTIMATE 2018.2 + (注意：务必使用 IDEA 开发，同时保证安装 lombok 插件)
- Mysql 5.7 + 

## 各 Module 介绍

| Module 名称                                                  | Module 介绍                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [demo-properties](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-properties) | spring-boot 读取配置文件中的内容                             |
| [demo-logback](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-logback) | spring-boot 集成 logback 日志                                |
| [demo-log-aop](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-log-aop) | spring-boot 使用 AOP 切面的方式记录 web 请求日志             |
| [demo-exception-handler](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-exception-handler) | spring-boot 统一异常处理，包括2种，第一种返回统一的 json 格式，第二种统一跳转到异常页面 |
| [demo-swagger](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-swagger) | spring-boot 集成原生的 `swagger` 用于统一管理、测试 API 接口 |
| [demo-knife4j](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-knife4j) | spring-boot 集成第三方 `knife4j` 美化API文档样式，用于统一管理、测试 API 接口 |
| [demo-login-operation-log](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-login-operation-log) | 自定义注解实现登录和操作日志的记录                           |
| [demo-upload-download](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-upload-download) | spring-boot 文件上传示例，包含本地文件上传                   |
| [demo-orm-mybatis](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-orm-mybatis) | spring-boot 集成原生mybatis，使用 mybatis-spring-boot-starter 集成，包括注解和xml俩种方式 |
| [demo-orm-mybatis-page](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-orm-mybatis-page) | pring-boot 集成通用Mapper和PageHelper                        |
| [demo-tree](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-tree) | 自定义注解实现树结构                                         |
| [demo-aop-call-info](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-aop-call-info) | 自定义注解实现参数和返回结果打印                             |
| [demo-easy-code](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-easy-code) | 使用插件`mybatisX`或者`easyCode`生成实体类和mapper           |
| [demo-orm-mybatis-plus](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-orm-mybatis-plus) | spring-boot 集成 `mybatis-plus`的相关配置以及条件构造器的使用 |
| [demo-redis-repeat-submit](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-redis-repeat-submit) | 自定义注解结合redis实现接口防止重复提交                      |
| [demo-cache-redis](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cache-redis) | spring-boot 整合 redis，操作redis中的数据，并使用redis缓存数据 |
| [demo-email](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-email) | spring-boot 整合 email，包括发送简单文本邮件、HTML邮件（包括模板HTML邮件）、附件邮件、静态资源邮件 |
| [demo-jasypt](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-jasypt) | 加密配置中的敏感信息                                         |
| [demo-task](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-task) | spring-boot 快速实现定时任务                                 |
| [demo-xxl-job](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-xxl-job) | springboot整合xxl-job                                        |
| [demo-xxl-job-http](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-xxl-job-http) | 通过http请求手动操作xxl-job                                  |
| [demo-xxl-job-auto-register](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-xxl-job-auto-register) | 手写starter完成xxl-job自动测试功能                           |
| [demo-xxl-job-auto-regiter-test](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-xxl-job-auto-register-test) | 测试手写starter完成xxl-job自动测试功能                       |
| [demo-async](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-async) | spring-boot 使用原生提供的异步任务支持，实现异步执行任务     |
| [demo-flyway](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-flyway) | spring boot 集成 Flyway，项目启动时初始化数据库表结构，同时支持数据库脚本版本控制 |
| [demo-redis-limit](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-redis-limit) | spring-boot 使用 Redis 实现分布式限流，保护 API              |
| [demo-jackson](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-jackson) | jackson在项目中各种注解的使用                                |
| [demo-multi-datasource-mybatis](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-multi-datasource-mybatis) | spring-boot 使用Mybatis集成多数据源，使用 Mybatis-Plus 提供的开源解决方案实现 |
| [demo-cache-redis-multi-datasource](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cache-redis-multi-datasource) | redis多数据源配置，单节点，哨兵，集群配置                    |
| [demo-druid](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-druid) | springboot整合Druid                                          |
| [demo-elasticsearch-rest-high-level-client](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-elasticsearch-rest-high-level-client) | spring boot 集成 ElasticSearch 7.x 版本，使用官方 Rest High Level Client 操作 ES 数据 |
| [demo-elasticsearch)](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-elasticsearch) | spring-boot 集成 ElasticSearch，集成 `spring-boot-starter-data-elasticsearch` 完成对 ElasticSearch 的高级使用技巧，包括创建索引、配置映射、删除索引、增删改查基本操作、复杂查询、高级查询、聚合查询等 |
| [demo-elasticsearch-easy-es](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-elasticsearch-easy-es) | springboot集成Easy-ES，简化`CRUD`及其它高阶操作,可以更好的帮助开发者减轻开发负担 |
| [demo-redis-limit-aop](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-redis-limit-aop) | 整合redis使用aop实现接口限流                                 |
| [demo-sharding-jdbc](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-sharding-jdbc) | spring-boot 使用 `sharding-jdbc` 实现分库分表，同时ORM采用 Mybatis-Plus |
| [demo-ureport2](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-ureport2) | spring boot 集成 Ureport2，实现中国式复杂报表设计            |
| [demo-validator](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-validator) | springboot整合validator实现请求参数校验                      |
| [demo-minIo](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-minIo) | springboot整合minIo文件存储系统                              |
| [demo-cache-redis-sentinel](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cache-redis-sentinel) | springboot整合redis哨兵集群                                  |
| [demo-sa-token](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-sa-token) | springboot整合satoken权限框架                                |
| [demo-docker](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-docker) | springboot整合docker构建镜像                                 |
| [demo-mapstruct-plus](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-mapstruct-plus) | springboot整合mapstruct-plus                                 |
| [demo-mapstruct](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-mapstruct) | springboot整合mapstruct                                      |
| [demo-utils](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-utils) | springboot中一些常用的工具类                                 |
| [demo-cache-redis-redisson](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cache-redis-redisson) | redisson分布式锁的应用(可重入锁、公平锁、联锁、红锁、读写锁、信号量、闭锁) |
| [demo-xss-jsoup](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-xss-jsoup) | springboot集成jsoup解决XSS安全问题                           |
| [demo-filter](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-filter) | springboot中自定义过滤器Filter使用详解                       |
| [demo-forest](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-forest) | springboot整合forest发送请求                                 |
| [demo-rabbitmq](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-rabbitmq) | springboot整合rabbitmq，包含入门、七种工作模式、生产者确认模式、消费者确认模式、重试机制、死信队列、延迟队列、以及案例（保证消息一致性，确保消息百分百被消费） |
| [demo-redis-online-count](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-redis-online-count) | springboot使用心跳机制+redis实现在线人数统计                 |
| [demo-anno-encrypt-body](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-springboot-starter/demo-anno-encrypt-body) | 自定义注解实现请求参数和响应结果的加密和解密                 |
| [demo-multi-thread](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-multi-thread) | Runnable和Callable的使用，CompletableFuture类的使用          |
| [demo-spring-security](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-springsecurity) | springboot整合security入门体验，自定义认证，动态url控制，整合jwt，访问控制方式，以及整合oauth2的四种模式demo |
| [demo-anno-redis-lock](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cluster-lock/demo-anno-redis-lock) | 自定义注解结合redis实现分布式定时任务锁                      |
| [demo-lock4j](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-cluster-lock/demo-lock4j) | springboot整合lock4j分布式锁                                 |
| [demo-i18n](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-i18n) | springboot整合国际化                                         |
| [easyexcel-write](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-poi/demo-easyexcel-write)，[easyexcel-read](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-poi/demo-easyexcel-read) | springboot整合easyexcel实现简单写，复杂头写，自定义格式转换，合并单元格，简单下拉框，联级下拉框，模版导出，以及自定义默认监听器，默认返回结果，和集成`validation`校验参数 |
| [demo-multi-datasource-mybatis-aop](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-multi-datasource-mybatis-aop) | 自定义注解集合druid和mybatis使用aop的方式实现动态数据源操作  |
| [demo-netty-socketio](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-netty-socketio) | springboot整合netty-socketio实现消息推送                     |



