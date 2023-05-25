# springboot-整合minio

`MinIO` 是一个基于`Apache License v2.0`开源协议的`对象存储`服务。它兼容亚马逊S3云存储服务接口，非常适合于存储`大容量非结构化`的数据，例如`图片`、`视频`、`日志文件`、`备份数据`和`容器/虚拟机镜像`等，而一个对象文件可以是任意大小，从几kb到最大5T不等。

MinIO官方文档：[https://docs.min.io/cn/](https://gitee.com/link?target=https%3A%2F%2Fdocs.min.io%2Fcn%2F)

## 一、部署minio

```yml
# 可参考 https://docs.min.io/docs/minio-docker-quickstart-guide.html
version: '3'
services:
  minio:
    image: minio/minio:latest                                    # 原镜像`minio/minio:latest`
    container_name: minio                                        # 容器名为'minio'
    restart: unless-stopped                                              # 指定容器退出后的重启策略为始终重启，但是不考虑在Docker守护进程启动时就已经停止了的容器
    volumes:                                                     # 数据卷挂载路径设置,将本机目录映射到容器目录
      - "./minio/data:/data"
      - "./minio/minio:/minio"
      - "./minio/config:/root/.minio"
    environment:                                      # 设置环境变量,相当于docker run命令中的-e
      TZ: Asia/Shanghai
      LANG: en_US.UTF-8
      MINIO_PROMETHEUS_AUTH_TYPE: "public"
      MINIO_ACCESS_KEY: "root"                        # 登录账号
      MINIO_SECRET_KEY: "password"                    # 登录密码
    command: server /data  --console-address ":9001"
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
    ports:                              # 映射端口
      - "9002:9000" # 文件上传&预览端口
      - "9001:9001" # 控制台访问端口

```

```shell
docker-compose -f docker-compose-minio.yml -p minio up -d
```

访问地址：[`ip地址:9001/minio`](https://gitee.com/link?target=http%3A%2F%2Fwww.zhengqingya.com%3A9001%2Fminio) 登录账号密码：`root/password`

**创建桶test,并且修改范围策略为公共的**

![image-20230525104834454](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230525104834454.png)

## 二、整合minio

### 1、pom.xml

新版本的minio需要下面俩个依赖，旧版本只需要`minio`

```xml
        <!-- minio文件服务器 -->
        <!-- https://mvnrepository.com/artifact/io.minio/minio -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>8.4.6</version>
        </dependency>

        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.8.1</version>
        </dependency>
```

### 2、application.yml

```yml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# ====================== ↓↓↓↓↓↓ MinIO文件服务器 ↓↓↓↓↓↓ ======================
minio:
  url: http://116.211.105.103:9002
  accessKey: root
  secretKey: password
  bucketName: test
```

### 3、MinIoProperties.java

```java
/**
 * MinIO属性类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIoProperties {

    /**
     * minio地址+端口号
     */
    private String url;

    /**
     * minio用户名
     */
    private String accessKey;

    /**
     * minio密码
     */
    private String secretKey;

    /**
     * 文件桶的名称
     */
    private String bucketName;

}
```

### 4、MinIoUtil.java

```java
package com.yolo.minio.config;

import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 * @description Java Client API参考文档：https://min.io/docs/minio/linux/developers/java/API.html
 */
@Slf4j
@Component
public class MinIoUtil {

    @Resource
    private MinIoProperties minIoProperties;

    private static MinioClient minioClient;

    /**
     * 初始化minio配置
     */
    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minIoProperties.getUrl())
                    .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                    .build();
            createBucket(minIoProperties.getBucketName());
        } catch (Exception e) {
            log.error("初始化minio配置异常：", e);
        }
    }

    // **************************** ↓↓↓↓↓↓ 桶操作 ↓↓↓↓↓↓ ****************************

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名
     * @return true:存在 false:不存在
     */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        boolean flag = false;
        try {
            flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        }catch (Exception e){
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名
     */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获取全部桶
     *
     * @return 桶信息
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        List<Bucket> bucketList = new ArrayList<>();
        try {
            bucketList = minioClient.listBuckets();
        }catch (Exception e){
            e.printStackTrace();
        }

        return bucketList;
    }

    // **************************** ↓↓↓↓↓↓ 文件操作 ↓↓↓↓↓↓ ****************************

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param file       文件
     * @return 文件url地址
     * @date 2020/8/16 23:40
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, MultipartFile file) {
        InputStream inputStream = null;
        String fileName = file.getOriginalFilename();
        fileName = fileName + System.currentTimeMillis();
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return getFileUrl(bucketName, fileName);
    }


    /**
     * description: 上传文件
     */
    public static List<String> upload(String bucketName,MultipartFile[] multipartFile) {
        List<String> names = new ArrayList<>(multipartFile.length);
        for (MultipartFile file : multipartFile) {
            String fileName = file.getOriginalFilename();
            if (StrUtil.isNotBlank(fileName)){
                String[] split = fileName.split("\\.");
                if (split.length > 1) {
                    fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
                } else {
                    fileName = fileName + System.currentTimeMillis();
                }
            }
            InputStream in = null;
            try {
                in = file.getInputStream();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(in, in.available(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            names.add(fileName);
        }
        return names;
    }


    /**
     * 删除文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return void
     * @date 2020/8/16 20:53
     */
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 下载文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @param response
     * @return void
     * @date 2020/8/17 0:34
     */
    @SneakyThrows(Exception.class)
    public static void download(String bucketName, String fileName, HttpServletResponse response) {
        // 获取对象的元数据
        InputStream is = null;
        try {
            final StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setContentType(stat.contentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            is = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            IOUtils.copy(is, response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取minio文件的预览地址
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return 预览地址
     * @date 2020/8/16 22:07
     */
    @SneakyThrows(Exception.class)
    public static String getFileUrl(String bucketName, String fileName) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .expiry(2, TimeUnit.HOURS)
                .build());
    }

}

```

### 5、MinIoController.java

```java
package com.yolo.minio.controller;

import com.yolo.minio.config.MinIoProperties;
import com.yolo.minio.config.MinIoUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * MinIO测试接口
 */
@RestController
@RequestMapping("/api/minio")
public class MinIoController {

    @Resource
    private MinIoProperties minIoProperties;

    @PostMapping(value = "/upload")
    public String upload(@RequestPart @RequestParam MultipartFile file) {
        return MinIoUtil.upload(minIoProperties.getBucketName(), file);
    }

    @PostMapping(value = "/upload1")
    public List<String> upload1(@RequestPart @RequestParam MultipartFile[] file) {
        List<String> upload = MinIoUtil.upload(minIoProperties.getBucketName(),file);
        List<String> path = new ArrayList<>();
        for (String s : upload) {
            path.add( MinIoUtil.getFileUrl(minIoProperties.getBucketName(),s));
        }
        return path;
    }

    @GetMapping(value = "/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        MinIoUtil.download(minIoProperties.getBucketName(), fileName, response);
    }

    @GetMapping(value = "/delete")
    public String delete(@RequestParam("fileName") String fileName) {
        MinIoUtil.deleteFile(minIoProperties.getBucketName(), fileName);
        return "删除成功";
    }

}

```



