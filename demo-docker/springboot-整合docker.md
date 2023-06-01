# springboot-整合docker

本 demo 主要演示了如何容器化一个 Spring Boot 项目。通过 `Dockerfile` 的方式打包成一个 images 

```
# 基础镜像
FROM openjdk:8-jdk-alpine

#复制jar包到容器中
COPY ./target/demo-docker.jar  /home/demo-docker.jar

# 暴露8080端口
EXPOSE 8080

# 启动镜像自动运行程序
# "--spring.config.location=/config/application.yml"
# "-Xmx1536m", "-Xms1536m", "-Duser.timezone=GMT+8"
CMD ["java",  "-jar", "/home/demo-docker.jar"]
```

![image-20230601203432360](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601203432360.png)

指定jar包的名称

### 手动打包

前往 Dockerfile 目录，打开命令行执行

```shell
docker build -t demo-docker:v0.01 .
```

查看生成镜像

![image-20230601203618879](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601203618879.png)

运行容器

```shell
docker run -d -p 8080:8080 --name demo demo-docker:v0.01
```

![image-20230601211133550](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230601211133550.png)

