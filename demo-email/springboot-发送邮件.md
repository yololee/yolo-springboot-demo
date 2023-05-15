# springboot-发送邮件

## 一、准备工作

> 本文采用网易邮箱发送邮件首先要申请开通POP3/SMTP服务

**步骤01** 登录网易邮箱，依次单击顶部的设置按钮

![image-20230515163116597](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515163116597.png)



**步骤02** 在账户选项卡 下方找到POP3/SMTP服务，单击后方的“开启”按钮

![image-20230515163146232](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515163146232.png)

然后扫码，发送短信获取授权码

## 二、环境搭建

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>


        <!-- Spring Boot 邮件依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        <!--jasypt配置文件加解密-->
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>2.1.1</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <!-- Spring Boot 模板依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```

### 2、application.yml

```yml
spring:
  mail:
    # 配置默认编码
    default-encoding: UTF-8
    # 配置邮件服务器地址
    # qq邮箱为smtp.qq.com          端口号465或587
    # sina    smtp.sina.cn
    # aliyun  smtp.aliyun.com
    # 163     smtp.163.com       端口号465或994
    host: smtp.163.com
    # 配置用户的账号
    username: huanglei421023@163.com
    # 配置密码,注意不是真正的密码，而是刚刚申请到的授权码
    # 使用 jasypt 加密密码，使用com.xkcoding.email.PasswordTest.testGeneratePassword 生成加密密码，替换 ENC(加密密码) 
    password: ENC(qOy+1HSuE1i38QbuTEGrACUf+AnUud1U4nCM1tMcCBs=)
    # 配置邮件服务器的端口(465或587)
    port: 465
    properties:
      mail:
        debug: true
        smtp:
          auth: 'true '
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
            port: 465
          ssl:
            enable: true
          starttls:
            enable: true
          statics:
            required: true
    protocol: smtp
# 为 jasypt 配置解密秘钥
jasypt:
  encryptor:
    password: spring-boot-demo
```

## 三、发送邮件

### 1、发送邮件通用方法

```java 
package com.yolo.email.service;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Component
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     */
    public void sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        if (ArrayUtil.isNotEmpty(cc)) {
            message.setCc(cc);
        }
        mailSender.send(message);
    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        mailSender.send(message);
    }

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人地址
     * @param subject  邮件主题
     * @param content  邮件内容
     * @param filePath 附件地址
     * @param cc       抄送地址
     * @throws MessagingException 邮件发送异常
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName, file);

        mailSender.send(message);
    }

    /**
     * 发送正文中有静态资源的邮件
     *
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param rscPath 静态资源地址
     * @param rscId   静态资源id
     * @param cc      抄送地址
     * @throws MessagingException 邮件发送异常
     */
    public void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        FileSystemResource res = new FileSystemResource(new File(rscPath));
        helper.addInline(rscId, res);

        mailSender.send(message);
    }
}
```

### 2、发送普通邮件

```java
    /**
     * 测试简单邮件
     */
    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail("2936412130@qq.com", "这是一封简单邮件", "这是一封普通的SpringBoot测试邮件");
    }
```

![image-20230515164209466](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515164209466.png)

### 3、发送HTML邮件

```java
    /**
     * 测试HTML邮件
     *
     * @throws MessagingException 邮件异常
     */
    @Test
    public void sendHtmlMail() throws MessagingException {
        Context context = new Context();
        context.setVariable("project", "Spring Boot Demo");
        context.setVariable("author", "yolo");
        context.setVariable("url", "https://gitee.com/huanglei1111/yolo-springboot-demo");

        String emailTemplate = templateEngine.process("welcome", context);
        mailService.sendHtmlMail("2936412130@qq.com", "这是一封模板HTML邮件", emailTemplate);
    }
```

![image-20230515164709972](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515164709972.png)

### 4、发送附件

```java
    /**
     * 测试附件邮件
     *
     * @throws MessagingException 邮件异常
     */
    @Test
    public void sendAttachmentsMail() throws MessagingException {
        URL resource = ResourceUtil.getResource("static/程序员头像.jpeg");
        mailService.sendAttachmentsMail("2936412130@qq.com", "这是一封带附件的邮件", "邮件中有附件，请注意查收！", resource.getPath());
    }
```

![image-20230515165130946](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515165130946.png)

### 5、发送静态资源邮件

```java
    /**
     * 测试静态资源邮件
     *
     * @throws MessagingException 邮件异常
     */
    @Test
    public void sendResourceMail() throws MessagingException {
        String rscId = "yolo";
        String content = "<html><body>这是带静态资源的邮件<br/><img src=\'cid:" + rscId + "\' ></body></html>";
        URL resource = ResourceUtil.getResource("static/yolo.jpeg");
        mailService.sendResourceMail("2936412130@qq.com", "这是一封带静态资源的邮件", content, resource.getPath(), rscId);
    }
```



![image-20230515165457005](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230515165457005.png)