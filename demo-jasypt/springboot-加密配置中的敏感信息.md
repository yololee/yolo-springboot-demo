# springboot-加密配置中的敏感信息

## 一、项目准备

### 1、pom.xml

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
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
        </dependency>
```

### 2、application.yml

```yml
yolo:
  datasource:
    password: DEC(123456)
# 为 jasypt 配置秘钥
jasypt:
  encryptor:
    password: yolo
```

## 二、加密解密

### 1、手动加密

```java
public class PasswordTest extends DemoJasyptApplicationTests {

    @Autowired
    private StringEncryptor encryptor;

    /**
     * 生成加密密码
     */
    @Test
    public void testGeneratePassword() {
        // 你的邮箱密码
        String password = "123456";
        // 加密后的密码(注意：配置上去的时候需要加 ENC(加密密码))
        String encryptPassword = encryptor.encrypt(password);
        String decryptPassword = encryptor.decrypt(encryptPassword);

        System.out.println("password = " + password);
        System.out.println("encryptPassword = " + encryptPassword);
        System.out.println("decryptPassword = " + decryptPassword);
    }
}

```

> 输出结果

```
password = 123456
encryptPassword = KKBN62wAZiY45tV/daomfutc8MUr8/mSb3tBpRuba7kLjgwC7DgG9KNS2xuudp62
decryptPassword = 123456
```

>  然后把密文替换配置信息的内容

```yml
yolo:
  datasource:
    password: ENC(KKBN62wAZiY45tV/daomfutc8MUr8/mSb3tBpRuba7kLjgwC7DgG9KNS2xuudp62)
# 为 jasypt 配置秘钥
jasypt:
  encryptor:
    password: yolo
```

```java
    @Value("${yolo.datasource.password}")
    private String password;

    @Test
    public void contextLoads() {
        System.out.println(password);//输出结果123456
    }
```

### 2、自动加密

在插件配置中加入：

```xml
<plugin>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-maven-plugin</artifactId>
    <version>3.0.3</version>
</plugin>
```

用`DEC()`将待加密内容包裹起来

```yml
yolo:
  redis:
    password: DEC(123456)
```

使用`jasypt-maven-plugin`插件来给`DEC()`包裹的内容实现批量加密

在终端中执行下面的命令：

```shell
 # 加密 因为这里我的配置文件是yml格式的需要制定位置
 mvn jasypt:encrypt -Djasypt.plugin.path="file:src/main/resources/application.yml" -Djasypt.encryptor.password="yolo"
 
 # 加密 如果配置文件是properties格式的就不需要制定位置
 mvn jasypt:encrypt  -Djasypt.encryptor.password="yolo"
 
 # 解密
 mvn jasypt:decrypt -Djasypt.encryptor.password="yolo"
```

> **注意**：这里`-Djasypt.encryptor.password`参数必须与配置文件中的一致，不然后面会解密失败

### 3、加密策略

1. 既然怕application文件被黑客窃取到，那么将密钥明文写在application中，那等于没有加密

2. 这里可以在启动SpringBoot应用的时候将密钥加入到启动命令中如下所示

   ```
   java -jar application.jar --jasypt.encryptor.password=YourPassword
   ```

   