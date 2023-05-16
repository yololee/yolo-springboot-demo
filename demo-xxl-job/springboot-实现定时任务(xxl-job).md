## 一、xxl-job-admin调度中心

> https://github.com/xuxueli/xxl-job.git

[docker部署xxl-job-admin](https://gitee.com/huanglei1111/docker-compose/tree/master/Linux/xxl-job)

![image-20230516140053842](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230516140053842.png)

> 访问地址：127.0.0.1:9003/xxl-job-admin/
>
> 用户名：admin
>
> 密码：123456

![image-20230516140331953](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230516140331953.png)

## 二、编写执行器项目

### 1、pom.xml

> 这里的xxl-job-core依赖版本最好跟xxl-job-admin的版本一致

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- xxl-job-core -->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-job-core</artifactId>
            <version>2.3.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

### 2、编写配置文件 application.yml

```yml
xxl:
  job:
    # 执行器通讯TOKEN [选填]：非空时启用；
    access-token:
    admin:
      #调度中心部署地址,多个配置逗号分隔 "http://address01,http://address02"
      # 调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
      address: http://localhost:9003/xxl-job-admin
    executor:
      # 执行器app名称,和控制台那边配置一样的名称，不然注册不上去
      # 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
      app-name: demo-task-xxl-job-executor
      # 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
      ip: 172.100.40.215
      # 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
      port: 9999
      # 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
      log-path: logs/demo-task-xxl-job/task-log
      # 执行器日志保存天数 [选填] ：值大于3时生效，启用执行器Log文件定期清理功能，否则不生效；
      log-retention-days: 30
```

> 1、这个地址是xxl-job-admin的地址
>
> xxl.job.admin.address=http://localhost:9003/xxl-job-admin   
>
> 2、这个是在xxl-job-admini上创建的执行器
>
> xxl.job.executor.app-name=demo-task-xxl-job-executor

### 3、编写 配置类 XxlJobProps.java

```java
package com.yolo.xxl.job.config;
/**
 * xxl-job 配置
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProps {
    /**
     * 调度中心配置
     */
    private XxlJobAdminProps admin;

    /**
     * 执行器配置
     */
    private XxlJobExecutorProps executor;

    /**
     * 与调度中心交互的accessToken
     */
    private String accessToken;

    @Data
    public static class XxlJobAdminProps {
        /**
         * 调度中心地址
         */
        private String address;
    }

    @Data
    public static class XxlJobExecutorProps {
        /**
         * 执行器名称
         */
        private String appName;

        /**
         * 执行器 IP
         */
        private String ip;

        /**
         * 执行器端口
         */
        private int port;

        /**
         * 执行器日志
         */
        private String logPath;

        /**
         * 执行器日志保留天数，-1
         */
        private int logRetentionDays;
    }
}
```

### 4、编写自动装配类 XxlConfig.java

```java
/**
 * xxl-job 自动装配
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobProps.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class XxlJobConfig {
    private final XxlJobProps xxlJobProps;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProps.getAdmin().getAddress());
        xxlJobSpringExecutor.setAccessToken(xxlJobProps.getAccessToken());
        xxlJobSpringExecutor.setAppname(xxlJobProps.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(xxlJobProps.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobProps.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(xxlJobProps.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProps.getExecutor().getLogRetentionDays());

        return xxlJobSpringExecutor;
    }

}
```

### 5、编写demoTask.java

```java
/**
 * 测试定时任务
 */
@Slf4j
@Component
public class DemoTask{

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @param param 定时任务参数
     * @return 执行状态
     * @throws Exception 任务异常
     */

    @XxlJob(value = "demo-test")
    public ReturnT<String> execute(String param) throws Exception {
        // 可以动态获取传递过来的参数，根据参数不同，当前调度的任务不同
        log.info("【param】= {}", param);
       log.info("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }
}
```

## 三、配置定时任务

### 1、新增执行器

![image-20230516141749419](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230516141749419.png)

### 2、新增task

![image-20230516141953908](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230516141953908.png)

### 3、启停定时任务

任务列表的操作列，拥有以下操作：执行、启动/停止、日志、编辑、删除

执行：单次触发任务，不影响定时逻辑

启动：启动定时任务

停止：停止定时任务

日志：查看当前任务执行日志

编辑：更新定时任务

删除：删除定时任务

### 4、测试

![image-20230516142438478](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230516142438478.png)



