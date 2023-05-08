# springboot-本地文件上传下载

## 一、项目准备

### 1、pom文件

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>

        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>
```

### 2、application.yml

```yml 
# 项目相关配置
yolo:
  # 文件路径 示例（ Windows配置D:/ruoyi/uploadPath，Linux配置 /home/ruoyi/uploadPath）
  fileUploadPath: demo-upload-download/uploadPath
  # 获取ip地址开关
  addressEnabled: false

# 开发环境配置
server:
  # 服务器的HTTP端口，默认为80
  port: 8080
  servlet:
    # 应用的访问路径
    context-path: /
  tomcat:
    # tomcat的URI编码
    uri-encoding: UTF-8
    # 连接数满后的排队数，默认为100
    accept-count: 1000
    threads:
      # tomcat最大线程数，默认为200
      max: 800
      # Tomcat启动初始化的线程数，默认值10
      min-spare: 100

# 日志配置
logging:
  level:
    com.yolo: debug

# Spring配置
spring:
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
  # 文件上传
  servlet:
    multipart:
      # 单个文件大小
      max-file-size:  10MB
      # 设置总上传的文件大小
      max-request-size:  20MB
```

3、通用上次下载类

```java
package com.yolo.file.controller;

import com.yolo.file.common.AjaxResult;
import com.yolo.file.common.Constants;
import com.yolo.file.config.ServerConfig;
import com.yolo.file.config.YoloConfig;
import com.yolo.file.util.FileUploadUtils;
import com.yolo.file.util.FileUtils;
import com.yolo.file.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 */
@Controller
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = YoloConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    @ResponseBody
    public AjaxResult uploadFiles(List<MultipartFile> files) {
        try {
            // 上传文件路径
            String filePath = YoloConfig.getUploadPath();
            List<String> fileNames = new ArrayList<>();
            List<String> newFileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileNames", StringUtils.join(fileNames, ","));
            ajax.put("newFileNames", StringUtils.join(newFileNames, ","));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     * http://localhost:8080/common/download/resource?resource=/profile/upload/2023/05/06/程序员头像_20230506095214A006.jpeg
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, Boolean delete,HttpServletResponse response) {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = YoloConfig.getFileUploadPath();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(downloadPath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}

```

> 其他一些工具类就不一一展示啦，详细内容看文章结尾的项目地址

## 二、测试上传

### 1、单文件上传

![image-20230508144118026](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508144118026.png)

![image-20230508144147787](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508144147787.png)

### 2、多文件上传

![image-20230508144459897](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508144459897.png)

## 三、测试下载

这里下载本地的文件，我们需要配置资源映射配置

```java
package com.yolo.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射
 * 生产使用为了安全,不建议通过这种方式映射,推荐使用nginx配置
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    public static final String RESOURCE_PREFIX  = "/profile";

    /**
     * Spring Boot 访问静态资源的位置(优先级按以下顺序)
     * classpath默认就是resources,所以classpath:/static/ 就是resources/static/
     * classpath:/META‐INF/resources/
     * classpath:/resources/
     * classpath:/static/
     * classpath:/public/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(RESOURCE_PREFIX + "/**")   //指的是对外暴露的访问路径
                .addResourceLocations("file:" + YoloConfig.getUploadPath()); //指的是内部文件放置的目录
    }
}
```

直接在浏览器输入我们上传成功返回的url

![image-20230508144810218](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508144810218.png)

或者访问通用下载接口进行下载

接口举例如下：

```
http://localhost:8080/common/download/resource?resource=/profile/upload/2023/05/06/程序员头像_20230506095214A006.jpeg
```

> [Gitee项目地址（demo-upload-download）](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-upload-download)

