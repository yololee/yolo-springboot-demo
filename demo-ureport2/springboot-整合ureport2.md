# springboot-整合ureport2

> 本 demo 主要演示了 Spring Boot 项目如何快速集成 ureport2 实现任意复杂的中国式报表功能。

UReport2 是一款基于架构在 Spring 之上纯 Java 的高性能报表引擎，通过迭代单元格可以实现任意复杂的中国式报表。 在 UReport2 中，提供了全新的基于网页的报表设计器，可以在 Chrome、Firefox、Edge 等各种主流浏览器运行（IE 浏览器除外）。使用 UReport2，打开浏览器即可完成各种复杂报表的设计制作

## 一、整合

因为官方没有提供一个 starter 包，需要自己集成，这里使用 [pig](https://github.com/pig-mesh/pig) 作者 冷冷同学开发的 starter 偷懒实现，这个 starter 不仅支持单机环境的配置，同时支持集群环境。

### pom.xml

```xml
<dependency>
  <groupId>com.pig4cloud.plugin</groupId>
  <artifactId>oss-spring-boot-starter</artifactId>
  <version>0.0.3</version>
</dependency>
```

### application.yml

```yml
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver
ureport:
  debug: false
  disableFileProvider: false
  disableHttpSessionReportCache: true
  # 单机模式，本地路径需要提前创建
  fileStoreDir: '/Users/huanglei/Desktop/work/giteeCode/yolo-springboot-demo/demo-ureport2/ureport2'
```

### 新增一个内部数据源

```java
/**
 * 内部数据源
 */
@Component
public class InnerDatasource implements BuildinDatasource {
    @Autowired
    private DataSource datasource;

    @Override
    public String name() {
        return "内部数据源";
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        return datasource.getConnection();
    }
}
```

### 初始化数据

```java
DROP TABLE IF EXISTS `t_user_ureport2`;
CREATE TABLE `t_user_ureport2` (
                                   `id` bigint(13) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `status` tinyint(4) NOT NULL COMMENT '是否禁用',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

BEGIN;
INSERT INTO `t_user_ureport2` VALUES (1, '测试人员 1', '2020-10-22 09:01:58', 1);
INSERT INTO `t_user_ureport2` VALUES (2, '测试人员 2', '2020-10-22 09:02:00', 0);
INSERT INTO `t_user_ureport2` VALUES (3, '测试人员 3', '2020-10-23 03:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (4, '测试人员 4', '2020-10-23 23:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (5, '测试人员 5', '2020-10-23 23:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (6, '测试人员 6', '2020-10-24 11:02:00', 0);
INSERT INTO `t_user_ureport2` VALUES (7, '测试人员 7', '2020-10-24 20:02:00', 0);
INSERT INTO `t_user_ureport2` VALUES (8, '测试人员 8', '2020-10-25 08:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (9, '测试人员 9', '2020-10-25 09:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (10, '测试人员 10', '2020-10-25 13:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (11, '测试人员 11', '2020-10-26 21:02:00', 0);
INSERT INTO `t_user_ureport2` VALUES (12, '测试人员 12', '2020-10-26 23:02:00', 1);
INSERT INTO `t_user_ureport2` VALUES (13, '测试人员 13', '2020-10-26 23:02:00', 1);
COMMIT;
```

## 二、测试

### 1、访问报表设计器

http://127.0.0.1:8080/demo/ureport/designer

![image-20230524113047708](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113047708.png)

### 2、选择数据源

我们这里选择我们刚刚自己设置的内部数据源

![image-20230524113301003](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113301003.png)

![image-20230524113414166](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113414166.png)

数据预览

![image-20230524113440730](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113440730.png)

点击确定，保存数据集

### 3、报表设计

![image-20230524113642663](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113642663.png)

#### 日期格式转换

![image-20230524113718890](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113718890.png)

#### 数据映射

![image-20230524113743487](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113743487.png)

### 4、预览

![image-20230524113840806](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113840806.png)

![image-20230524113855822](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113855822.png)

### 5、保存

![image-20230524113923093](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524113923093.png)

点击保存之后，你本地在 `application.yml` 文件中配置的地址就会出现一个 `test.ureport.xml` 文件

下次可以直接通过 http://localhost:8080/demo/ureport/preview?_u=file:test.ureport.xml 这个地址预览报表了

## 三、集群使用

如上文设计好的模板是保存在服务本机的，在集群环境中需要使用统一的文件系统存储。

### 新增依赖

```
<dependency>
  <groupId>com.pig4cloud.plugin</groupId>
  <artifactId>oss-spring-boot-starter</artifactId>
  <version>0.0.3</version>
</dependency>
```

### 仅需配置云存储相关参数, 演示为minio

```
oss:
  access-key: lengleng
  secret-key: lengleng
  bucket-name: lengleng
  endpoint: http://minio.pig4cloud.com
```

> 注意：这里使用的是冷冷提供的公共 minio，请勿乱用，也不保证数据的可靠性，建议小伙伴自建一个minio，或者使用阿里云 oss

> - [ureport2 使用文档](https://www.w3cschool.cn/ureport)
> - [ureport-spring-boot-starter](https://github.com/pig-mesh/ureport-spring-boot-starter) UReport2 的 spring boot 封装
> - [oss-spring-boot-starter](https://github.com/pig-mesh/oss-spring-boot-starter) 兼容所有 S3 协议的分布式文件存储系统

