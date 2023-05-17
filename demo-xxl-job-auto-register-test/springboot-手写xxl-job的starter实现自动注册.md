# springboot-手写xxl-job的starter实现自动注册

## 一、思路分析

需求：项目启动时主动注册`执行器`和`任务`到调度中心

1. 项目启动，注册器是否注册到调度中心，没有注册，自动添加新的执行器，已经注册，扫描所有添加`@XxlJob的JobHandler`
2. 检查各个`**JobHandler**`是否注册到调度中心，是，跳过，否则创建新的任务

> [xxl-job官方地址](https://gitee.com/xuxueli0323/xxl-job/tree/master)

![image-20230517143155049](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517143155049.png)

在这俩个controller中可以找到关键的几个方法

- `/jobgroup/pageList`：执行器列表的条件查询
- `/jobgroup/save`：添加执行器
- `/jobinfo/pageList`：任务列表的条件查询
- `/jobinfo/add`：添加任务

## 二、实现自定义starter

### 1、依赖

> 之后这个项目打包到本地仓库，或者远程仓库之后需要注意版本冲突问题

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--关键依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>

        <!--关键依赖-->
        <!-- xxl-job-core -->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-job-core</artifactId>
            <version>2.3.0</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.4.5</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

### 2、接口调用

#### 登录接口

创建一个`JobLoginService`，在调用业务接口前，需要通过登录接口获取`cookie`，并在获取到`cookie`后，缓存到本地的`Map`中

其他接口在调用时，直接从缓存中获取`cookie`，如果缓存中不存在则调用`/login`接口，为了避免这一过程失败，允许最多重试3次

```java
@Service
public class JobLoginServiceImpl implements JobLoginService {

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    @Value("${xxl.job.admin.username}")
    private String username;

    @Value("${xxl.job.admin.password}")
    private String password;

    private final Map<String,String> loginCookie=new HashMap<>();

    @Override
    public void login() {
        String url=adminAddress+"/login";
        HttpResponse response = HttpRequest.post(url)
                .form("userName",username)
                .form("password",password)
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> "XXL_JOB_LOGIN_IDENTITY".equals(cookie.getName())).findFirst();
        if (!cookieOpt.isPresent()) {
            throw new RuntimeException("get xxl-job cookie error!");
        }

        String value = cookieOpt.get().getValue();
        loginCookie.put("XXL_JOB_LOGIN_IDENTITY",value);
    }

    @Override
    public String getCookie() {
        for (int i = 0; i < 3; i++) {
            String cookieStr = loginCookie.get("XXL_JOB_LOGIN_IDENTITY");
            if (cookieStr !=null) {
                return "XXL_JOB_LOGIN_IDENTITY="+cookieStr;
            }
            login();
        }
        throw new RuntimeException("get xxl-job cookie error!");
    }
}
```

#### 执行器接口

```java
@Service
public class JobGroupServiceImpl implements JobGroupService {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupServiceImpl.class);

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    @Value("${xxl.job.executor.app-name}")
    @NotBlank
    @Length(max = 64)
    private String appName;

    @Value("${xxl.job.executor.title}")
    @NotBlank
    @Length(max = 12)
    private String title;

    /*
     * 执行器地址类型：0=自动注册、1=手动录入
     * */
    @Value("${xxl.job.executor.addressType:0}")
    private Integer addressType;

    /*
     * 执行器地址列表，多地址逗号分隔(手动录入)
     * */
    @Value("${xxl.job.executor.addressList:}")
    private String addressList;

    @Autowired
    private JobLoginService jobLoginService;

    private final static String JOB_GROUP_URI = "/jobgroup";


    /**
     * 根据appName和执行器名称title查询执行器列表
     *
     * @return {@link List}<{@link XxlJobGroup}>
     */
    @Override
    public List<XxlJobGroup> getJobGroup() {
        HttpResponse response = HttpUtil.createPost(adminAddress + JOB_GROUP_URI + "/pageList")
                .form("appname", appName)
                .form("title", title)
                .cookie(jobLoginService.getCookie())
                .execute();

        XxlJobActuatorInfo xxlJobActuatorInfo = JSONUtil.toBean(JSONUtil.parseObj(response.body()), XxlJobActuatorInfo.class);
        List<XxlJobGroup> data = xxlJobActuatorInfo.getData();
        return data;
    }

    /**
     * 注册新executor到调度中心
     *
     * @return boolean
     */
    @Override
    public boolean autoRegisterGroup() {
        HttpRequest httpRequest = HttpUtil.createPost(adminAddress + JOB_GROUP_URI + "/save")
                .form("appname", appName)
                .form("title", title)
                .form("addressType", addressType);

        //0=自动注册、1=手动录入
        if (addressType.equals(1)) {
            if (Strings.isBlank(addressList)) {
                throw new RuntimeException("手动录入模式下,执行器地址列表不能为空");
            }
            httpRequest.form("addressList", addressList);
        }

        HttpResponse response = httpRequest.cookie(jobLoginService.getCookie())
                .execute();
        Object code = JSONUtil.parse(response.body()).getByPath("code");
        logger.info("添加执行器状态：" + response.body());
        return code.equals(200);
    }


    /**
     * 精确检查
     * 根据 appName和title
     *
     * @return boolean
     */
    @Override
    public boolean preciselyCheck() {
        //原生xxl-job-admin中分页模糊查询
        List<XxlJobGroup> jobGroup = getJobGroup();
        if (CollUtil.isEmpty(jobGroup)){
            return false;
        }
        Optional<XxlJobGroup> has = jobGroup.stream()
                .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(appName)
                        && xxlJobGroup.getTitle().equals(title))
                .findAny();
        //isPresent()方法用于判断value是否存在，不为NULL则返回true
        return has.isPresent();
    }

}
```

#### 任务接口

```java
@Service
public class JobInfoServiceImpl implements JobInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupServiceImpl.class);

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    private final static String JOB_INFO_URI = "/jobinfo";

    @Autowired
    private JobLoginService jobLoginService;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        HttpResponse response = HttpUtil.createPost(adminAddress+JOB_INFO_URI+"/pageList")
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("triggerStatus", -1)
                .cookie(jobLoginService.getCookie())
                .execute();

        JSONArray array = JSONUtil.parse(response.body()).getByPath("data", JSONArray.class);
        List<XxlJobInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Integer addJobInfo(XxlJobInfo xxlJobInfo) {
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpUtil.createPost(adminAddress+JOB_INFO_URI+"/add")
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();

        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (code.equals(200)){
            logger.info("新增任务成功");
            return Convert.toInt(json.getByPath("content"));
        }
        throw new RuntimeException("add jobInfo error!");
    }

}
```

### 3、创建新注解

在创建任务时，必填字段除了执行器和`jobHandler`之外，还有**任务描述**、**负责人**、**Cron表达式**、**调度类型**、**运行模式**。在这里，我们默认调度类型为`CRON`、运行模式为`BEAN`，另外的3个字段的信息需要用户指定。

因此我们需要创建一个新注解`@XxlRegister`，来配合原生的`@XxlJob`注解进行使用，填写这几个字段的信息：

最后，额外添加了一个`triggerStatus`属性，表示任务的默认调度状态，0为停止状态，1为运行状态。

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlRegister {

    String cron();

    String jobDesc() default "default jobDesc";

    String author() default "default Author";

    /*
     * 默认为 ROUND 轮询方式
     * 可选： FIRST 第一个
     * */
    String executorRouteStrategy() default "ROUND";

    //调度状态：0-停止，1-运行
    int triggerStatus() default 0;
}
```

### 4、自动注册核心

基本准备工作做完后，下面实现自动注册执行器和`jobHandler`的核心代码。核心类实现`ApplicationListener`接口，在接收到`ApplicationReadyEvent`事件后开始执行自动注册逻辑。

```java
package com.yolo.auto.register.core;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yolo.auto.register.annotation.XxlRegister;
import com.yolo.auto.register.service.JobGroupService;
import com.yolo.auto.register.service.JobInfoService;
import com.yolo.auto.register.model.XxlJobGroup;
import com.yolo.auto.register.model.XxlJobInfo;
import groovy.util.logging.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Component
public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>,
        ApplicationContextAware {

    private static final Log log =LogFactory.get();

    private ApplicationContext applicationContext;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //注册执行器
        addJobGroup();
        //注册任务
        addJobInfo();
    }

    /**
     * 自动注册执行器
     * 配置文件中的appName和title精确匹配查看调度中心是否已有执行器被注册过了，如果存在则跳过，不存在则新注册一个
     */
    private void addJobGroup() {
        //存在直接返回
        if (jobGroupService.preciselyCheck()) {
            return;
        }

        //不存在新增执行器
        if(jobGroupService.autoRegisterGroup()) {
            log.info("auto register xxl-job group success!");
        }
    }


    /**
     * 添加任务
     * 1、通过applicationContext拿到spring容器中的所有bean，再拿到这些bean中所有添加了@XxlJob注解的方法
     * 2、对上面获取到的方法进行检查，是否添加了我们自定义的@XxlRegister注解，如果没有则跳过，不进行自动注册
     * 3、对同时添加了@XxlJob和@XxlRegister的方法，通过执行器id和jobHandler的值判断是否已经在调度中心注册过了，如果已存在则跳过
     * 4、对于满足注解条件且没有注册过的jobHandler，调用接口注册到调度中心
     */
    private void addJobInfo() {
        //获取执行器
        List<XxlJobGroup> jobGroups = jobGroupService.getJobGroup();
        XxlJobGroup xxlJobGroup = jobGroups.get(0);

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, XxlJob> annotatedMethods  = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
                    if (!jobInfo.isEmpty()){
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent()) {
                            continue;
                        }
                    }

                    XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
                    jobInfoService.addJobInfo(xxlJobInfo);
                }
            }
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister){
        XxlJobInfo xxlJobInfo=new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobGroup.getId());
        xxlJobInfo.setJobDesc(xxlRegister.jobDesc());
        xxlJobInfo.setAuthor(xxlRegister.author());
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(xxlRegister.cron());
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlJob.value());
        xxlJobInfo.setExecutorRouteStrategy(xxlRegister.executorRouteStrategy());
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setTriggerStatus(xxlRegister.triggerStatus());

        return xxlJobInfo;
    }

}
```

### 5、自动装配

创建一个配置类，用于扫描`bean`：

```java
@Configuration
@ComponentScan(basePackages = "com.yolo.auto.register")
public class XxlJobPlusConfig {
}
```

将它添加到`META-INF/spring.factories`文件：

```factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.yolo.auto.register.config.XxlJobPlusConfig
```

到这里`starter`的编写就完成了，可以通过maven发布jar包到本地或者私服

```shell
# 安装在本地可以使用命令，或者maven插件
mvn clean
mvn install
```

本地仓库就可以看到

![image-20230517145857326](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517145857326.png)

## 三、测试

### 1、新项目中引入依赖

```xml
        <dependency>
            <groupId>com.yolo</groupId>
            <artifactId>xxl-job-auto-register</artifactId>
            <version>0.0.2</version>
        </dependency>
```

### 2、application.properties

> 这个非必填的配置`xxl.job.executor.addressList`，如果填写的话，其中的ip和端口需要跟这个`xxl.job.executor.ip`和`xxl.job.executor.port`的ip跟端口一样，不然会出现拒绝连接的错误

```properties
# xxl-job-admin调度中心中没有配置，这里也可以不配置
xxl.job.access-token=
# 调度中心部署地址,多个配置逗号分隔 "http://address01,http://address02"
# 调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
xxl.job.admin.address=http://localhost:9003/xxl-job-admin
# 执行器app名称,和控制台那边配置一样的名称，不然注册不上去
# 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
xxl.job.executor.app-name=demo-auto-register-executor
# 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
xxl.job.executor.ip=
# 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
xxl.job.executor.port=9999
# 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
xxl.job.executor.log-path=logs/demo-task-xxl-job/task-log
# 执行器日志保存天数 [选填] ：值大于3时生效，启用执行器Log文件定期清理功能，否则不生效；
xxl.job.executor.log-retention-days=30


# 新增配置项，必须项
# admin用户名
xxl.job.admin.username=admin
# admin 密码
xxl.job.admin.password=123456
# 执行器名称
xxl.job.executor.title=TestRegister

# 新增配置项，可选项
# 执行器地址类型：0=自动注册、1=手动录入，默认为0
xxl.job.executor.addressType=1
# 在上面为1的情况下，手动录入执行器地址列表，多地址逗号分隔
xxl.job.executor.addressList=http://172.100.40.215:9999/
```

### 3、添加注解

```java
/**
 * 测试定时任务
 */
@Slf4j
@Component
public class DemoTask {

    @XxlJob(value = "demo-auto-register-test")
    @XxlRegister(cron = "0/2 * * * * ?", author = "yolo", jobDesc = "测试auto-register")
    public ReturnT<String> execute(String param) throws Exception {
        // 可以动态获取传递过来的参数，根据参数不同，当前调度的任务不同
        log.info("【param】= {}", param);
        log.info("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @XxlJob(value = "demo-test")
    @XxlRegister(cron = "0/2 * * * * ?", author = "yolo", jobDesc = "demo-test",triggerStatus = 1)
    public ReturnT<String> execute2(String param) throws Exception {
        // 可以动态获取传递过来的参数，根据参数不同，当前调度的任务不同
        log.info("【param】= {}", param);
        log.info("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }
}
```

![image-20230517150622262](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517150622262.png)

![image-20230517150645737](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517150645737.png)

![image-20230517150705786](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230517150705786.png)

