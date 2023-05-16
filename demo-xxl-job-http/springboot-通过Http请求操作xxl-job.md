# springboot-通过Http请求操作xxl-job

### 1、pom.xml

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

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
        </dependency>
```

### 2、application.yml

> 这里的 jobGroupId 需要在xxl-job-admin控制中心中创建执行器

```yml
xxl:
  job:
    login:
      address: http://127.0.0.1:9003/xxl-job-admin
      username: admin
      password: 123456
      # 执行器组id
      jobGroupId: 2
```

### 3、XxlJobProps.java

```java
@Data
@Component
@ConfigurationProperties(prefix = "xxl.job.login")
@EnableConfigurationProperties(XxlJobProps.class) //使 使用 @ConfigurationProperties 注解的类生效
public class XxlJobProps {

    private String address;
    private String username;
    private String password;

    private Integer jobGroupId;

}
```

### 4、XxlJobApiUtils.java

```java
package com.yolo.xxl.job.http.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.yolo.xxl.job.http.model.XxlJobInfo;
import com.yolo.xxl.job.http.model.XxlJobResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.*;

/**
 * xxl-job api 操作工具类
 *
 * @author jujueaoye
 * @date 2023/05/16
 */
@Component
@Slf4j
public class XxlJobApiUtils {

    @Autowired
    private  XxlJobProps xxlJobProps;

    private final static String JOB_INFO_URI = "/jobinfo";
    private final static String JOB_GROUP_URI = "/jobgroup";

    private final Map<String,String> loginCookie=new HashMap<>();

    /*************************************************登录相关**********************************************************/
    private void login() {
        String url=xxlJobProps.getAddress()+"/login";


        HttpResponse response = HttpUtil.createPost(url)
                .form("userName", xxlJobProps.getUsername())
                .form("password", xxlJobProps.getPassword())
                .execute();

        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> "XXL_JOB_LOGIN_IDENTITY".equals(cookie.getName())).findFirst();
        if (!cookieOpt.isPresent()){
            throw new RuntimeException("get xxl-job cookie error!");
        }
        String value = cookieOpt.get().getValue();
        loginCookie.put("XXL_JOB_LOGIN_IDENTITY",value);
    }

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


    /*************************************************任务管理管理**********************************************************/


    //任务列表
    public String xxlJobList(){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/pageList")
                .form("triggerStatus","-1") //'调度状态：0-停止，1-运行'
                .form("jobGroup",xxlJobProps.getJobGroupId())// 执行器id
                .cookie(getCookie())
                .execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * 手动保存任务
     * @param xxlJobInfo 参数
     * @return 任务id
     */
    public String xxlJobAdd(XxlJobInfo xxlJobInfo){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/add")
                .form("jobGroup",xxlJobProps.getJobGroupId()) //执行器id
                .form("jobDesc",xxlJobInfo.getJobDesc())// 任务描述
                .form("author",xxlJobInfo.getAuthor())// 作者
                .form("scheduleType","CRON")//调度类型
                .form("scheduleConf",xxlJobInfo.getScheduleConf())// Cron
                .form("glueType", GlueTypeEnum.BEAN)// 运行模式
                .form("executorHandler",xxlJobInfo.getExecutorHandler())// JobHandler
                .form("executorRouteStrategy","ROUND")// 路由策略
                .form("misfireStrategy","DO_NOTHING")// 调度过期策略
                .form("executorBlockStrategy", ExecutorBlockStrategyEnum.SERIAL_EXECUTION)//阻塞处理策略
                .form("executorParam",xxlJobInfo.getExecutorParam())// 任务参数
                .cookie(getCookie())
                .execute();

        XxlJobResponseInfo info = JSONUtil.toBean(JSONUtil.parseObj(execute.body()), XxlJobResponseInfo.class);
        if (Objects.isNull(info) || !info.getCode().equals(HttpStatus.OK.value())){
            throw new RuntimeException("手动保存任务失败");
        }

        log.info("【execute】= {}", info);
        return info.getContent();
    }

    /**
     * 手动触发一次任务
     * @param id 任务id
     * @param executorParam 任务参数
     */
    public void xxlJobTrigger(Integer id,String executorParam){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/trigger")
                .form("id",id) //任务id
                .form("executorParam",executorParam)// 任务参数
                .cookie(getCookie())
                .execute();

        XxlJobResponseInfo info = JSONUtil.toBean(JSONUtil.parseObj(execute.body()), XxlJobResponseInfo.class);
        if (Objects.isNull(info) || !info.getCode().equals(HttpStatus.OK.value())){
            throw new RuntimeException("手动触发一次任务失败");
        }
        log.info("【execute】= {}", info);
    }

    /**
     * 手动删除任务
     * @param id 任务id
     */
    public void xxlJobRemove(Integer id){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/remove")
                .form("id",id) //任务id
                .cookie(getCookie())
                .execute();

        XxlJobResponseInfo info = JSONUtil.toBean(JSONUtil.parseObj(execute.body()), XxlJobResponseInfo.class);
        if (Objects.isNull(info) || !info.getCode().equals(HttpStatus.OK.value())){
            throw new RuntimeException("手动删除任务失败");
        }

        log.info("【execute】= {}", info);
    }

    /**
     * 手动停止任务
     * @param id 任务id
     */
    public void xxlJobStop(Integer id){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/stop")
                .form("id",id) //任务id
                .cookie(getCookie())
                .execute();

        XxlJobResponseInfo info = JSONUtil.toBean(JSONUtil.parseObj(execute.body()), XxlJobResponseInfo.class);
        if (Objects.isNull(info) || !info.getCode().equals(HttpStatus.OK.value())){
            throw new RuntimeException("手动触发一次任务");
        }

        log.info("【execute】= {}", execute);
    }

    //手动开始任务
    public void xxlJobStart(Integer id){
        HttpResponse execute = HttpUtil.createGet(xxlJobProps.getAddress() + JOB_INFO_URI + "/start")
                .form("id",id) //任务id
                .cookie(getCookie())
                .execute();
        XxlJobResponseInfo info = JSONUtil.toBean(JSONUtil.parseObj(execute.body()), XxlJobResponseInfo.class);
        if (Objects.isNull(info) || !info.getCode().equals(HttpStatus.OK.value())){
            throw new RuntimeException("手动开始任务失败");
        }
        log.info("【execute】= {}", info);
    }
}
```

