# springboot-logback的使用

## 1、默认日志 logback

默认情况下，springboot会使用logback来记录日志，并且使用INFO级别输出到控制台，在运行程序的时候，你应该看到过很多这样INFO级别的日志了。

![image-20230504151105231](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504151105231.png)


从上图可以看到，日志输入的内容如下：

- 时间日期：精确到毫秒
- 日志级别：ERROR，WARN，INFO，DEBUG，TRACE
- 进程ID：
- 分隔符：— 标识实际日志的开始
- 线程名：方括号括起来(可能会截断控制台输出)
- Logger名：通常使用源代码的类名
- 日志内容：

## 2、添加依赖

假如maven依赖中添加了`spring-boot-starter-logging`：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>
```

但是呢，实际开发中我们不需要直接添加该依赖。
你会发现`spring-boot-starter`或者`spring-boot-starter-web`其中包含了 `spring-boot-starter-logging`，该依赖内容就是 Spring Boot 默认的日志框架 `logback`。

![](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504151209512.png)


## 3、控制台输出

日志级别从低到高：

```
TRACE < DEBUG < INFO < WARN < ERROR < FATAL
```

如果设置为 `WARN` ，则低于 `WARN` 的信息都不会输出。
`Spring Boot`中默认配置`ERROR`、`WARN`和`INFO`级别的日志输出到控制台。

您还可以通过启动您的应用程序 `--debug` 标志来启用“调试”模式（开发的时候推荐开启）,以下两种方式皆可：

- 在运行命令后加入`--debug`标志，如：`$ java -jar springTest.jar --debug`
- 在`application.properties`中配置`debug=true`，该属性置为`true`的时候，核心`Logger`（包含嵌入式容器、hibernate、spring）会输出更多内容，但是你**自己应用的日志并不会输出为`DEBUG`级别**


这里也验证了上面的观点，springboot默认把`ERROR`、`WARN`和`INFO`级别的日志输出到控制台，

```java
//日志的类名必须是当前类，如果不是当前类，那么输出日志的类名也是错的
private final Logger logger = LoggerFactory.getLogger(Hl13LogApplicationTests.class);
```

这里每一个类都需要写上面的，这样很麻烦，可以使用注解`@Slf4j`，可是需要使用lombok


可以使用`{}` 占位符来拼接字符串，而不需要使用`““+””`来连接字符串

![image-20230504151722450](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230504151722450.png)


## 4、文件输出

默认情况下，springboot将日志输出到控制台，不会写到日志文件。

使用`Spring Boot`喜欢在`application.properties`或`application.yml`配置，这样只能配置简单的场景，保存路径、日志格式等。复杂的场景（区分 info 和 error 的日志、每天产生一个日志文件等）满足不了，只能自定义配置。

### 4.1控制台格式化输出内容

```properties
# 格式化，只输出日期和内容
logging.pattern.console= "%d -%p -%m" %n
```


打印参数: Log4J采用类似C语言中的printf函数的打印格式格式化日志信息，如下:

```
%m   输出代码中指定的消息
%p   输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL 
%r   输出自应用启动到输出该log信息耗费的毫秒数 
%c   输出所属的类目，通常就是所在类的全名 
%t   输出产生该日志事件的线程名 
%n   输出一个回车换行符，Windows平台为“\\r\\n”，Unix平台为“\\n” 
%d   输出日志时间点的日期或时间,默认格式为ISO8601,也可以在其后指定格式，比如：		%d{yyy MMM dd HH:mm:ss,SSS}，输出类似：2018年6月15日22：10：28，921  
%l   输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。举例：		Testlog4.main(TestLog4.java: 10)
```

### 4.2日志输出路径

这里设置输出路径为`E:\log`

```properties
# 日志输出路径
logging.path= E:\\log
```

默认会在设置的 `path` 生成一个`spring.log` 文件。


如果要编写除控制台输出之外的日志文件，则需在`application.properties`中设置`logging.file`或`logging.path`属性。

- `logging.file`，设置文件，可以是绝对路径，也可以是相对路径。如：`logging.file=my.log`
- `logging.path`，设置目录，会在该目录下创建spring.log文件，并写入日志内容，如：`logging.path=E:\\log`
  如果只配置` logging.file`，会在项目的当前路径下生成一个 `xxx.log` 日志文件。
  如果只配置` logging.path`，在 `E:\\log`文件夹生成一个日志文件为 `spring.log`

```properties
#这样会在当前项目下生成my.log文件
logging.file=my.log

# 这样会在指定目录生成my.log文件
# logger文件夹需要提前生成
logging.file= src\\main\\resources\\logger\\my.log
```

> 注：二者不能同时使用，如若同时使用，则只有`logging.file`生效
> 默认情况下，日志文件的大小达到`10MB`时会切分一次，产生新的日志文件，默认级别为：`ERROR、WARN、INFO`

## 5、级别控制

所有支持的日志记录系统都可以在`Spring`环境中设置记录级别（例如在`application.properties`中）
格式为：`'logging.level.* = LEVEL'`

`logging.level`：日志级别控制前缀，*为包名或Logger名

`LEVEL`：选项`TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF`

```properties
#com.mye.hl13log包下所有class以DEBUG级别输出
logging.level.com.yolo.logback=DEBUG
```