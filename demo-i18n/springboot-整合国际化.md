# springboot：整合国际化

### 一、pom文件

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.26</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.18</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

### 二、国际化文件

![image-20230810095813397](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230810095813397.png)

> messages.properties

```properties
#错误消息
not.null=* 必须填写
user.jcaptcha.error=验证码错误
user.jcaptcha.expire=验证码已失效
user.not.exists=对不起, 您的账号：{0} 不存在.
user.password.not.match=用户不存在/密码错误
user.password.retry.limit.count=密码输入错误{0}次
user.password.retry.limit.exceed=密码输入错误{0}次，帐户锁定{1}分钟
user.password.delete=对不起，您的账号：{0} 已被删除
user.blocked=对不起，您的账号：{0} 已禁用，请联系管理员
role.blocked=角色已封禁，请联系管理员
user.logout.success=退出成功
length.not.valid=长度必须在{min}到{max}个字符之间
user.username.not.blank=用户名不能为空
user.username.not.valid=* 2到20个汉字、字母、数字或下划线组成，且必须以非数字开头
user.username.length.valid=账户长度必须在{min}到{max}个字符之间
user.password.not.blank=用户密码不能为空
user.password.length.valid=用户密码长度必须在{min}到{max}个字符之间
user.password.not.valid=* 5-50个字符
user.email.not.valid=邮箱格式错误
user.email.not.blank=邮箱不能为空
user.phonenumber.not.blank=用户手机号不能为空
user.mobile.phone.number.not.valid=手机号格式错误
user.login.success=登录成功
user.register.success=注册成功
user.register.save.error=保存用户 {0} 失败，注册账号已存在
user.register.error=注册失败，请联系系统管理人员
user.notfound=请重新登录
user.forcelogout=管理员强制退出，请重新登录
user.unknown.error=未知错误，请重新登录
##文件上传消息
upload.exceed.maxSize=上传的文件大小超出限制的文件大小！<br/>允许的文件最大大小是：{0}MB！
upload.filename.exceed.length=上传的文件名最长{0}个字符
##权限
no.permission=您没有数据的权限，请联系管理员添加权限 [{0}]
no.create.permission=您没有创建数据的权限，请联系管理员添加权限 [{0}]
no.update.permission=您没有修改数据的权限，请联系管理员添加权限 [{0}]
no.delete.permission=您没有删除数据的权限，请联系管理员添加权限 [{0}]
no.export.permission=您没有导出数据的权限，请联系管理员添加权限 [{0}]
no.view.permission=您没有查看数据的权限，请联系管理员添加权限 [{0}]
repeat.submit.message=不允许重复提交，请稍候再试
rate.limiter.message=访问过于频繁，请稍候再试
sms.code.not.blank=短信验证码不能为空
sms.code.retry.limit.count=短信验证码输入错误{0}次
sms.code.retry.limit.exceed=短信验证码输入错误{0}次，帐户锁定{1}分钟
email.code.not.blank=邮箱验证码不能为空
email.code.retry.limit.count=邮箱验证码输入错误{0}次
email.code.retry.limit.exceed=邮箱验证码输入错误{0}次，帐户锁定{1}分钟
xcx.code.not.blank=小程序code不能为空

```

> messages_en_US.properties

```properties
#错误消息
not.null=* Required fill in
user.jcaptcha.error=Captcha error
user.jcaptcha.expire=Captcha invalid
user.not.exists=Sorry, your account: {0} does not exist
user.password.not.match=User does not exist/Password error
user.password.retry.limit.count=Password input error {0} times
user.password.retry.limit.exceed=Password input error {0} times, account locked for {1} minutes
user.password.delete=Sorry, your account：{0} has been deleted
user.blocked=Sorry, your account: {0} has been disabled. Please contact the administrator
role.blocked=Role disabled，please contact administrators
user.logout.success=Exit successful
length.not.valid=The length must be between {min} and {max} characters
user.username.not.blank=Username cannot be blank
user.username.not.valid=* 2 to 20 chinese characters, letters, numbers or underscores, and must start with a non number
user.username.length.valid=Account length must be between {min} and {max} characters
user.password.not.blank=Password cannot be empty
user.password.length.valid=Password length must be between {min} and {max} characters
user.password.not.valid=* 5-50 characters
user.email.not.valid=Mailbox format error
user.email.not.blank=Mailbox cannot be blank
user.phonenumber.not.blank=Phone number cannot be blank
user.mobile.phone.number.not.valid=Phone number format error
user.login.success=Login successful
user.register.success=Register successful
user.register.save.error=Failed to save user {0}, The registered account already exists
user.register.error=Register failed, please contact system administrator
user.notfound=Please login again
user.forcelogout=The administrator is forced to exit，please login again
user.unknown.error=Unknown error, please login again
##文件上传消息
upload.exceed.maxSize=The uploaded file size exceeds the limit file size！<br/>the maximum allowed file size is：{0}MB！
upload.filename.exceed.length=The maximum length of uploaded file name is {0} characters
##权限
no.permission=You do not have permission to the data，please contact your administrator to add permissions [{0}]
no.create.permission=You do not have permission to create data，please contact your administrator to add permissions [{0}]
no.update.permission=You do not have permission to modify data，please contact your administrator to add permissions [{0}]
no.delete.permission=You do not have permission to delete data，please contact your administrator to add permissions [{0}]
no.export.permission=You do not have permission to export data，please contact your administrator to add permissions [{0}]
no.view.permission=You do not have permission to view data，please contact your administrator to add permissions [{0}]
repeat.submit.message=Repeat submit is not allowed, please try again later
rate.limiter.message=Visit too frequently, please try again later
sms.code.not.blank=Sms code cannot be blank
sms.code.retry.limit.count=Sms code input error {0} times
sms.code.retry.limit.exceed=Sms code input error {0} times, account locked for {1} minutes
email.code.not.blank=Email code cannot be blank
email.code.retry.limit.count=Email code input error {0} times
email.code.retry.limit.exceed=Email code input error {0} times, account locked for {1} minutes
xcx.code.not.blank=Mini program code cannot be blank

```

> messages_zh_CN.properties

```properties
#错误消息
not.null=* 必须填写
user.jcaptcha.error=验证码错误
user.jcaptcha.expire=验证码已失效
user.not.exists=对不起, 您的账号：{0} 不存在.
user.password.not.match=用户不存在/密码错误
user.password.retry.limit.count=密码输入错误{0}次
user.password.retry.limit.exceed=密码输入错误{0}次，帐户锁定{1}分钟
user.password.delete=对不起，您的账号：{0} 已被删除
user.blocked=对不起，您的账号：{0} 已禁用，请联系管理员
role.blocked=角色已封禁，请联系管理员
user.logout.success=退出成功
length.not.valid=长度必须在{min}到{max}个字符之间
user.username.not.blank=用户名不能为空
user.username.not.valid=* 2到20个汉字、字母、数字或下划线组成，且必须以非数字开头
user.username.length.valid=账户长度必须在{min}到{max}个字符之间
user.password.not.blank=用户密码不能为空
user.password.length.valid=用户密码长度必须在{min}到{max}个字符之间
user.password.not.valid=* 5-50个字符
user.email.not.valid=邮箱格式错误
user.email.not.blank=邮箱不能为空
user.phonenumber.not.blank=用户手机号不能为空
user.mobile.phone.number.not.valid=手机号格式错误
user.login.success=登录成功
user.register.success=注册成功
user.register.save.error=保存用户 {0} 失败，注册账号已存在
user.register.error=注册失败，请联系系统管理人员
user.notfound=请重新登录
user.forcelogout=管理员强制退出，请重新登录
user.unknown.error=未知错误，请重新登录
##文件上传消息
upload.exceed.maxSize=上传的文件大小超出限制的文件大小！<br/>允许的文件最大大小是：{0}MB！
upload.filename.exceed.length=上传的文件名最长{0}个字符
##权限
no.permission=您没有数据的权限，请联系管理员添加权限 [{0}]
no.create.permission=您没有创建数据的权限，请联系管理员添加权限 [{0}]
no.update.permission=您没有修改数据的权限，请联系管理员添加权限 [{0}]
no.delete.permission=您没有删除数据的权限，请联系管理员添加权限 [{0}]
no.export.permission=您没有导出数据的权限，请联系管理员添加权限 [{0}]
no.view.permission=您没有查看数据的权限，请联系管理员添加权限 [{0}]
repeat.submit.message=不允许重复提交，请稍候再试
rate.limiter.message=访问过于频繁，请稍候再试
sms.code.not.blank=短信验证码不能为空
sms.code.retry.limit.count=短信验证码输入错误{0}次
sms.code.retry.limit.exceed=短信验证码输入错误{0}次，帐户锁定{1}分钟
email.code.not.blank=邮箱验证码不能为空
email.code.retry.limit.count=邮箱验证码输入错误{0}次
email.code.retry.limit.exceed=邮箱验证码输入错误{0}次，帐户锁定{1}分钟
xcx.code.not.blank=小程序code不能为空
```

### 三、国际化配置

```java
package com.yolo.demo.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 国际化配置
 */
@Configuration
public class I18nConfig {

    @Bean
    public LocaleResolver localeResolver() {
        return new I18nLocaleResolver();
    }

    /**
     * 获取请求头国际化信息
     */
    static class I18nLocaleResolver implements LocaleResolver {

        @Override
        public Locale resolveLocale(HttpServletRequest httpServletRequest) {
            String language = httpServletRequest.getHeader("content-language");
            Locale locale = Locale.getDefault();
            if (StrUtil.isNotBlank(language)) {
                String[] split = language.split("_");
                locale = new Locale(split[0], split[1]);
            }
            return locale;
        }

        @Override
        public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

        }
    }
}
```

```yml
spring:
  # 资源信息
  messages:
    # 国际化资源文件路径
    basename: i18n/messages
```

### 四、测试

```java
@Validated
@RestController
@RequestMapping("/demo/i18n")
public class TestI18nController {

    /**
     * 通过code获取国际化内容
     * code为 messages.properties 中的 key
     * <p>
     * 测试使用 user.register.success
     *
     * @param code 国际化code
     */
    @GetMapping()
    public R<Void> get(String code) {
        return R.ok(MessageUtils.message(code));
    }

    /**
     * Validator 校验国际化
     * 不传值 分别查看异常返回
     * <p>
     * 测试使用 not.null
     */
    @GetMapping("/test1")
    public R<Void> test1(@NotBlank(message = "{not.null}") String str) {
        return R.ok(str);
    }

    /**
     * Bean 校验国际化
     * 不传值 分别查看异常返回
     * <p>
     * 测试使用 not.null
     */
    @GetMapping("/test2")
    public R<TestI18nBo> test2(@Validated TestI18nBo bo) {
        return R.ok(bo);
    }

    @Data
    public static class TestI18nBo {

        @NotBlank(message = "{not.null}")
        private String name;

        @NotNull(message = "{not.null}")
        @Range(min = 0, max = 100, message = "{length.not.valid}")
        private Integer age;
    }
}
```

![image-20230810100004463](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230810100004463.png)

