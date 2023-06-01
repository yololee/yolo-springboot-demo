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