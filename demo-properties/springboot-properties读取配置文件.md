# springboot-properties 获取配置文件的方法

配置文件准备

application.yml

```yml 
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  profiles:
    active: prod
```

application-dev.yml

```yml 
application:
  name: dev环境 @artifactId@
  version: dev环境 @version@
developer:
  name: dev环境 yolo
  website: dev环境 https://gitee.com/huanglei1111
  qq: dev环境 123456
  phone-number: dev环境 123456
```

application-prod.yml

```yml
application:
  name: prod环境 @artifactId@
  version: prod环境 @version@
developer:
  name: dev环境 yolo
  website: dev环境 https://gitee.com/huanglei1111
  qq: dev环境 123123
  phone-number: dev环境 123123
```

## 使用 @Value 

```java
@Data
@Component
public class ApplicationProperty {
	@Value("${application.name}")
	private String name;
	@Value("${application.version}")
	private String version;
}
```

```
@SpringBootApplication
public class DemoPropertiesApplication implements InitializingBean {

    @Autowired
    private ApplicationProperty applicationProperty;

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("方式一：@Value " + applicationProperty.getName() + " - " + applicationProperty.getVersion());
    }
}
```

![image-20230504094732274](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504094732274.png)

## 使用 @ConfigurationProperties

@ConfigurationProperties 和 @Value 的使用略微不同，@Value 是读取单个配置项的，而 @ConfigurationProperties 是读取一组配置项的，我们可以使用 @ConfigurationProperties 加实体类读取一组配置项

```java
@Data
@ConfigurationProperties(prefix = "developer")
@Component
public class DeveloperProperty {
	private String name;
	private String website;
	private String qq;
	private String phoneNumber;
}
```

其中 prefix 表示读取一组配置项的根 name，相当于 Java 中的类名，最后再把此配置类，注入到某一个类中就可以使用了

```
@SpringBootApplication
public class DemoPropertiesApplication implements InitializingBean {

    @Autowired
    private DeveloperProperty developerProperty;

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("方式二：@ConfigurationProperties " + developerProperty.getName() + " - " + developerProperty.getQq());
    }
}
```

![image-20230504095221995](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504095221995.png)

## 使用 Environment 

Environment 是 Spring Core 中的一个用于读取配置文件的类，将此类使用 @Autowired 注入到类中就可以使用它的 getProperty 方法来获取某个配置项的值

```java
@SpringBootApplication
public class DemoPropertiesApplication implements InitializingBean {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("方式三：Environment " + environment.getProperty("developer.qq"));
    }
}
```

![image-20230504095524696](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504095524696.png)

## 使用 @PropertySource

使用 @PropertySource 注解可以用来指定读取某个配置文件，比如指定读取 application-dev.yml 配置文件的配置内容

```java
@SpringBootApplication
@PropertySource("classpath:application-prod.yml")
public class DemoPropertiesApplication implements InitializingBean {

    @Value("${developer.qq}")
    private String qq;

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("方式四：Environment " + qq);
    }
}
```

![image-20230504100022862](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504100022862.png)

如果配置文件中出现中文乱码的情况，可通过指定[编码格式](https://so.csdn.net/so/search?q=编码格式&spm=1001.2101.3001.7020)的方式来解决中文乱码的问题，具体实现如下：

```java
@PropertySource(value = "dev.properties", encoding = "utf-8")
```

## 使用原生方式读取配置文件

我们还可以使用最原始的方式 Properties 对象来读取配置文件，只支持读取后缀为properties格式的数据

> application.properties

```properties
server.port=8081
```

```java
package com.yolo.properties;

import com.yolo.properties.config.ApplicationProperty;
import com.yolo.properties.config.DeveloperProperty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@SpringBootApplication
public class DemoPropertiesApplication implements InitializingBean {

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Properties props = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("application.properties")),
                    StandardCharsets.UTF_8);
            props.load(inputStreamReader);
        } catch (IOException e1) {
            System.out.println(e1);
        }
        System.out.println("方式五：Properties Name：" + props.getProperty("server.port"));
    }
}

```

![image-20230504101014850](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504101014850.png)