# springboot-整合forest发送请求

## 一、介绍

`Forest`是一个`高层`的、`极简`的`轻量级` `HTTP调用API框架`，让Java发送`HTTP/HTTPS`请求不再难。它比OkHttp和HttpClient更高层，比Feign更轻量，是封装调用第三方restful api client接口的好帮手

> 相比于直接使用Httpclient我们不再写一大堆重复的代码了，而是像调用本地方法一样去发送HTTP请求

[forest项目地址](https://gitee.com/dromara/forest?_from=gitee_search)

[forest中文文档](https://forest.dtflyx.com/)

## 二、整合

### 1、pom.xml

```xml
<dependency>
    <groupId>com.dtflys.forest</groupId>
    <artifactId>forest-spring-boot-starter</artifactId>
    <version>1.5.31</version>
</dependency>
```

### 2、application.yml

```yml
server:
  port: 8081

# ========================== ↓↓↓↓↓↓ forest配置 ↓↓↓↓↓↓ ==========================
forest:
  bean-id: config0             # 在spring上下文中bean的id，默认值为forestConfiguration
  backend: okhttp3             # 后端HTTP框架（默认为 okhttp3）
  max-connections: 1000        # 连接池最大连接数（默认为 500）
  max-route-connections: 500   # 每个路由的最大连接数（默认为 500）
  max-request-queue-size: 100  # [自v1.5.22版本起可用] 最大请求等待队列大小
  max-async-thread-size: 300   # [自v1.5.21版本起可用] 最大异步线程数
  max-async-queue-size: 16     # [自v1.5.22版本起可用] 最大异步线程池队列大小
  timeout: 3000                # [已不推荐使用] 请求超时时间，单位为毫秒（默认为 3000）
  connect-timeout: 3000        # 连接超时时间，单位为毫秒（默认为 timeout）
  read-timeout: 3000           # 数据读取超时时间，单位为毫秒（默认为 timeout）
  max-retry-count: 0           # 请求失败后重试次数（默认为 0 次不重试）
  ssl-protocol: TLS            # 单向验证的HTTPS的默认TLS协议（默认为 TLS）
  log-enabled: true            # 打开或关闭日志（默认为 true）
  log-request: true            # 打开/关闭Forest请求日志（默认为 true）
  log-response-status: true    # 打开/关闭Forest响应状态日志（默认为 true）
  log-response-content: true   # 打开/关闭Forest响应内容日志（默认为 false）
  async-mode: platform         # [自v1.5.27版本起可用] 异步模式（默认为 platform）
  variables:
    host: 127.0.0.1      # 声明全局变量，变量名: host，变量值: 127.0.0.1
    port: 8080       # 声明全局变量，变量名: port，变量值: 8080
```

### 3、启动类

```java
@SpringBootApplication
@ForestScan(basePackages = "com.yolo.demo.rpc")// forest扫描远程接口所在的包名
public class DemoForestApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoForestApplication.class, args);
    }
}
```

## 三、接口编写介绍

### 1、请求方法

Forest 使用不同的**请求注解**来标识某个接口方法来进行发送不同类型的请求，下面展示几个常见的请求注解，具体的注解请看官方文档

> @Get：获取资源
>
> @Post：传输实体文本
>
> @Put：上传资源
>
> @Delete：删除资源
>
> @Request：可动态传入HTTP方法

若不想在接口定义的时候直接定死为某个具体的 HTTP 请求方法，而是想从全局变量或方法参数中动态传入

```java
/**
 * 通过在 @Request 注解的 type 属性中定义字符串模板
 * 在字符串模板中引用方法的参数
 */
@Request(
    url = "http://localhost:8080/hello",
    type = "{type}"
)
String simpleRequest(@Var("type") String type);

```

在调用改方法时通过参数传入 HTTP 请求方法类型（字符串类型，大小写不敏感）

```java
// POST 请求
String result1 = simpleRequest("post");
// DELETE 请求
String result2 = simpleRequest("DELETE");
```

### 2、请求地址

#### 直接填写

> 在`url`属性中填入完整的请求地址

```java
    @Request("http://127.0.0.1:8080/forest/hello")
    String simpleRequest();
```

#### `@Var注解`

> 通过 `@Var` 注解修饰的参数从外部动态传入`URL`，完成参数或者参数的一部分

```java
/**
 * 整个完整的URL都通过 @Var 注解修饰的参数动态传入
 */
@Get("{myURL}")
String send2(@Var("myURL") String myURL);

/**
 * 通过参数转入的值作为URL的一部分
 */
@Get("http://{myURL}/abc")
String send3(@Var("myURL") String myURL);
```

#### @Address 注解

Forest 从`1.5.3`版本开始提供了 `@Address` 注解，帮助您将URL的地址部分提取出来，方便管理

```java
    @Address(host = "{0}", port = "{1}")
    @Get("/forest/hello")
    String send3(String host, int port);
```

或者在接口上写，表示这个类都应用，这个host和port，不过方法上定义的可以覆盖接口上定义的

```java
@Component
@Address(host = "${host}",port = "${port}")
public interface MyClient {
    @Get("/forest/hello")
    String send4();
}
```

#### @BaseRequest 注解

注解定义在接口类上，在`@BaseRequest`上定义的属性会被分配到该接口中每一个方法上，但方法上定义的请求属性会覆盖`@BaseRequest`上重复定义的内容

```java
/**
 * 若全局变量中已定义 baseUrl
 * 便会将全局变量中的值绑定到 @BaseRequest 的属性中
 */
@BaseRequest(baseURL = "${baseURL}")
@Component
public interface MyClient {
    @Get("/forest/hello")
    String send4();
}
```

### 3、URL参数

#### @Query 注解

> @Query 注解修饰的参数一定会出现在 URL 中
>
> 需要单个单个定义 `参数名=参数值` 的时候，@Query注解的value值一定要有，比如 @Query("name") String name
>
> 需要绑定对象的时候，@Query注解的value值一定要空着，比如 @Query User user 或 @Query Map map

```java
/**
 * 使用 @Query 注解，可以直接将该注解修饰的参数动态绑定到请求url中
 * 注解的 value 值即代表它在url的Query部分的参数名
 */
@Get("http://localhost:8080/abc?id=0")
String send(@Query("a") String a, @Query("b") String b);


/**
 * 使用 @Query 注解，可以修饰 Map 类型的参数
 * 很自然的，Map 的 Key 将作为 URL 的参数名， Value 将作为 URL 的参数值
 * 这时候 @Query 注解不定义名称
 */
@Get("http://localhost:8080/abc?id=0")
String send1(@Query Map<String, Object> map);


/**
 * @Query 注解也可以修饰自定义类型的对象参数
 * 依据对象类的 Getter 和 Setter 的规则取出属性
 * 其属性名为 URL 参数名，属性值为 URL 参数值
 * 这时候 @Query 注解不定义名称
 */
@Get("http://localhost:8080/abc?id=0")
String send2(@Query UserInfo user);
```

#### 数组参数

```java
/*
 * 接受列表参数为URL查询参数
 */
@Get("http://localhost:8080/abc")
String send1(@Query("id") List idList);

//       http://localhost:8080/abc?id=1&id=2&id=3&id=4
```

### 4、请求体

#### 表单格式

```java
/**
 * contentType属性设置为 application/x-www-form-urlencoded 即为表单格式，
 * 当然不设置的时候默认值也为 application/x-www-form-urlencoded， 也同样是表单格式。
 * 在 @Body 注解的 value 属性中设置的名称为表单项的 key 名，
 * 而注解所修饰的参数值即为表单项的值，它可以为任何类型，不过最终都会转换为字符串进行传输。
 */
@Post(
    url = "http://localhost:8080/user",
    contentType = "application/x-www-form-urlencoded"
)
String sendPost(@Body("key1") String value1,  @Body("key2") Integer value2, @Body("key3") Long value3);

/**
 * contentType 属性不设置默认为 application/x-www-form-urlencoded
 * 要以对象作为表达传输项时，其 @Body 注解的 value 名称不能设置
 */
@Post("http://localhost:8080/hello/user")
String send(@Body User user);

```

#### Json格式

```java
/**
 * 被@JSONBody注解修饰的参数会根据其类型被自定解析为JSON字符串
 * 使用@JSONBody注解时可以省略 contentType = "application/json"属性设置
 */
@Post("http://localhost:8080/hello/user")
String helloUser(@JSONBody User user);


/**
 * 按键值对分别修饰不同的参数
 * 这时每个参数前的 @JSONBody 注解必须填上 value 属性或 name 属性的值，作为JSON的字段名称
 */
@Post("http://localhost:8080/hello/user")
String helloUser(@JSONBody("username") String username, @JSONBody("password") String password);


/**
 * 被@JSONBody注解修饰的Map类型参数会被自定解析为JSON字符串
 */
@Post(url = "http://localhost:8080/hello/user")
String helloUser(@JSONBody Map<String, Object> user);

```

#### 二进制格式

```java
/**
 * 发送Byte数组类型数据
 */
@Post(
        url = "/upload/${filename}",
        contentType = "application/octet-stream"
)
String sendByteArryr(@Body byte[] body, @Var("filename") String filename);

/**
 * 发送File类型数据
 */
@Post(
    url = "/upload/${filename}",
    contentType = "application/octet-stream"
)
String sendFile(@Body File file, @Var("filename") String filename);

/**
 * 发送输入流类型数据
 */
@Post(
    url = "/upload/${filename}",
    contentType = "application/octet-stream"
)
String sendInputStream(@Body InputStream inputStream, @Var("filename") String filename);
```

### 5、响应结果

#### 反序列化

第一步：定义`dataType`属性

`dataType`属性指定了该请求响应返回的数据类型，目前可选的数据类型有三种: `text`, `json`, `xml`

Forest会根据您指定的`dataType`属性选择不同的反序列化方式。其中`dataType`的默认值为`text`，如果您不指定其他数据类型，那么Forest就不会做任何形式的序列化，并以文本字符串的形式返回给你数据

第二步：指定反序列化的目标类型

```java
/**
 * dataType属性指明了返回的数据类型为JSON
 */
@Get(
    url = "http://localhost:8080/user?id=${0}",
    dataType = "json"
)
User getUser(Integer id)

```

#### 返回响应对象

```java
    @Post(value = "/forest/user/list",dataType = "json")
    ForestResponse<ApiResponse> send7();
```

```java
// 以ForestResponse类型变量接受响应数据
ForestResponse<String> response = client.postUser(user);

// 用isError方法去判断请求是否失败
if (response.isError()) {
    ... ...
}

// 用isSuccess方法去判断请求是否成功
if (response.isSuccess()) {
    ... ...
}

// 以字符串方式读取请求响应内容
String text = response.readAsString();

// getContent方法可以获取请求响应内容文本
// 和readAsString方法不同的地方在于，getContent方法不会读取二进制形式数据内容，
// 而readAsString方法会将二进制数据转换成字符串读取
String content = response.getContent();

// 获取反序列化成对象类型的请求响应内容
// 因为返回类型为ForetReponse<String>, 其泛型参数为String
// 所以这里也用String类型获取结果        
String result = response.getResult();

// 以字节数组的形式获取请求响应内容
byte[] byteArray = response.getByteArray();

// 以输入流的形式获取请求响应内容
InputStream in = response.getInputStream();
```

