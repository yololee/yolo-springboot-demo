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
| [demo-call-info](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-call-info) | 自定义注解实现参数和返回结果打印                             |
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
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |
|                                                              |                                                              |