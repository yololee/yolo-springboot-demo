# springboot-自定义注解实现请求参数和响应体的加密和解密

## 一、前言

- **请求解密：** 前端在请求后端之前，将`body`参数进行`AES`对称加密，并且将加密的`key`写进request的header中，后端获取请求参数以及header中的key，进行解密操作；
- **响应加密：**后端在往前端返回数据前，先对参数进行加密，并且进行`AES`对称加密`key`写进respose的header中，前端获取响应参数以及header中的key，进行解密数据；
- **实现类：** 通过实现`RequestBodyAdvice`接口，对前端请求的参数进行解密并且重新，让真实结构的数据进入到Controller中；通过实现`ResponseBodyAdvice`接口，将响应的参数进行AES加密，返回到前端
- **自定义注解**： 对于一个请求是否需要继续解密、返回的参数是否需要加密，通过自定义注解`DecryptRequest`与`EncryptResponse`实现，注解可以作用在类或者方法上，如果类和方法都存在以方法上的注解为准，注解中的value默认为true，如果对某个方法不想进行加密或者解密那么value就设置为false，比如:`@EncryptResponse(value = false)`;

## 二、实现

### 工具类

> `EncryptUtil`实现对请求的解密、响应数据的加密处理

```java
package com.yolo.demo.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {
    public final static String AES = "AES";

    /**
     * 设置为CBC加密，默认情况下ECB比CBC更高效
     */
    private final static String CBC = "/CBC/PKCS5Padding";


    public static void main(String[] args) {
        /**
         * 偏移量，AES 128位数据块对应偏移量为16位，任意值即可
         */
        String offset = "5LiN6KaB56C06Kej";

        String input = "123";
        String encryptAes = encryptBySymmetry(input, offset, EncryptUtil.AES);
        System.out.println("AES加密:" + encryptAes);
        String aes = decryptBySymmetry(encryptAes, offset, EncryptUtil.AES);
        System.out.println("AES解密:" + aes);
    }

    /**
     * 对称加密
     *
     * @param input     : 密文
     * @param offset    : 偏移量
     * @param algorithm : 类型：DES、AES
     * @return
     */
    public static String encryptBySymmetry(String input, String offset, String algorithm) {
        try {
            // 根据加密类型判断key字节数
            checkAlgorithmAndKey(offset, algorithm);

            // CBC模式
            String transformation = algorithm + CBC;
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 创建加密规则
            // 第一个参数key的字节
            // 第二个参数表示加密算法
            SecretKeySpec sks = new SecretKeySpec(offset.getBytes(), algorithm);

            // 使用CBC模式
            IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, sks, iv);


            // 加密
            byte[] bytes = cipher.doFinal(input.getBytes());

            // 输出加密后的数据
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加密失败！");
        }
    }

    /**
     * 对称解密
     *
     * @param input     : 密文
     * @param offset    : 偏移量
     * @param algorithm : 类型：DES、AES
     * @return
     */
    public static String decryptBySymmetry(String input, String offset, String algorithm) {
        try {
            // 根据加密类型判断key字节数
            checkAlgorithmAndKey(offset, algorithm);

            // CBC模式
            String transformation = algorithm + CBC;

            // 1,获取Cipher对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 指定密钥规则
            SecretKeySpec sks = new SecretKeySpec(offset.getBytes(), algorithm);
            // 默认采用ECB加密：同样的原文生成同样的密文
            // CBC加密：同样的原文生成的密文不一样

            // 使用CBC模式
            IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, sks, iv);

            // 3. 解密，上面使用的base64编码，下面直接用密文
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(input));
            //  因为是明文，所以直接返回
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解密失败！");
        }
    }


    private static void checkAlgorithmAndKey(String key, String algorithm) {
        // 根据加密类型判断key字节数
        int length = key.getBytes().length;
        boolean typeEnable = false;
        if (AES.equals(algorithm)) {
            typeEnable = length == 16;
        } else {
            throw new RuntimeException("加密类型不存在");
        }
        if (!typeEnable) {
            throw new RuntimeException("加密Key错误");
        }
    }
}

```

### 自定义注解

```java
@Target({ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequest {
    /**
     * 是否对body进行解密
     */
    boolean value() default true;
}
```

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {
    /**
     * 是否对结果加密
     */
    boolean value() default true;
}

```

### 加密处理器

```java
package com.yolo.demo.handler;


import com.yolo.demo.annotation.DecryptRequest;
import com.yolo.demo.annotation.EncryptResponse;
import org.springframework.core.MethodParameter;

import java.util.Objects;


public class EncryptionHandler {

    public static String IGNORE_HEADER_NAME = "ignoreParam";
    public static String ENCRYPT_KEY = "encrypt";

    /**
     * 判断是否对响应参数进行加密
     *
     * @param returnType
     * @return
     */
    public static boolean checkEncrypt(MethodParameter returnType) {
        boolean flag = false;
        EncryptResponse classPresent = returnType.getContainingClass().getAnnotation(EncryptResponse.class);
        if (classPresent != null) {
            // 类上标注的是否需要解密
            flag = classPresent.value();

        }
        EncryptResponse methodPresent = Objects.requireNonNull(returnType.getMethod()).getAnnotation(EncryptResponse.class);
        if (methodPresent != null) {
            // 方法上标注的是否需要解密
            flag = methodPresent.value();
        }
        return flag;
    }

    /**
     * 判断是否对请求参数解密
     *
     * @param returnType
     * @return
     */
    public static boolean checkDecrypt(MethodParameter returnType) {
        try {
            boolean flag = false;
            DecryptRequest classPresent = returnType.getContainingClass().getAnnotation(DecryptRequest.class);
            if (classPresent != null) {
                //类上标注的是否需要解密
                flag = classPresent.value();
            }

            // 判断方法上是否有加密注解
            DecryptRequest methodPresent = Objects.requireNonNull(returnType.getMethod()).getAnnotation(DecryptRequest.class);
            if (methodPresent != null) {
                //方法上标注的是否需要解密
                flag = methodPresent.value();
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

```

### 请求解密

```java
package com.yolo.demo.handler.request;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.yolo.demo.handler.EncryptionHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;


@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return EncryptionHandler.checkDecrypt(methodParameter);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        HttpHeaders headers = inputMessage.getHeaders();
        // 对特定请求的header不进行进行解密（一般为swagger、feign请求）
        List<String> str = headers.get(EncryptionHandler.IGNORE_HEADER_NAME);
        if (str != null && str.size() > 0) {
            return inputMessage;
        }
        // 进行解密处理，返回解密后的参数
        // 获取请求加密key
        String key = headers.getFirst("key");
        return new DecryptHttpInputMessage(inputMessage, key);
    }


    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}

```

```java
package com.yolo.demo.handler.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.yolo.demo.handler.EncryptionHandler;
import com.yolo.demo.util.EncryptUtil;
import com.yolo.demo.util.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;


@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {
    private final HttpInputMessage inputMessage;
    private final String secretKey;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String secretKey) {
        this.inputMessage = inputMessage;
        this.secretKey = secretKey;
    }

    @Override
    public InputStream getBody() {
        try {
            InputStream body = inputMessage.getBody();
            String content = getStringByInputStream(body);

            // content转map,获取密文
            Map<String, String> map = JsonUtils.jsonToHashMap(content,String.class);
            if (CollectionUtils.isEmpty(map)){
                throw new RuntimeException("请求体位空");
            }

            String cipherText = map.get(EncryptionHandler.ENCRYPT_KEY);

            // 解密密文
            // 获取偏移量
            String offset = secretKey.substring(10, 26);
            String decryptBody = EncryptUtil.decryptBySymmetry(cipherText, offset, EncryptUtil.AES);
            return new ByteArrayInputStream(decryptBody.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("请求数据处理失败！");
        }

    }

    @Override
    public HttpHeaders getHeaders() {
        return inputMessage.getHeaders();
    }

    public static String getStringByInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
        } catch (Exception e) {
            inputStream.close();
            outputStream.close();
        }
        return outputStream.toString();
    }

}

```

### 响应加密

```java
package com.yolo.demo.handler.response;

import java.math.BigInteger;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.yolo.demo.handler.EncryptionHandler;
import com.yolo.demo.util.EncryptUtil;
import com.yolo.demo.util.UUIDUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        return EncryptionHandler.checkEncrypt(returnType);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            HttpHeaders headers = request.getHeaders();
            // 对特定请求的header不进行加密（一般为swagger、feign请求）
            List<String> str = headers.get(EncryptionHandler.IGNORE_HEADER_NAME);
            if (str != null && str.size() > 0) {
                return body;
            }

            String result;
            Class<?> dataClass = body.getClass();
            if (dataClass.isPrimitive() || (body instanceof String)) {
                result = String.valueOf(body);
            } else {
                SerializeConfig serializeConfig = SerializeConfig.globalInstance;
                serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
                serializeConfig.put(Long.class, ToStringSerializer.instance);
                result = JSON.toJSONString(body, serializeConfig, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
            }

            String key = UUIDUtils.getUuId().toUpperCase();
            // 设置响应的加密key,用于前端解密
            response.getHeaders().add(EncryptionHandler.ENCRYPT_KEY, key);

            // 对数据加密返回
            String offset = key.substring(10, 26);
            return EncryptUtil.encryptBySymmetry(result, offset, EncryptUtil.AES);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据响应错误!");
        }
    }

}

```

### 代码实现

```java
package com.yolo.demo.controller;


import com.alibaba.fastjson.JSON;
import com.yolo.demo.util.EncryptUtil;
import com.yolo.demo.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/encrypt")
@Slf4j
public class EncryptionController {


    /**
     * 将明文数据加密为密文，并且返回加密时的key
     * 其实就是模拟前端请求时的加密数据
     *
     * @param text 明文数据
     * @return
     */
    @GetMapping("/jia")
    public String jia(String text) {
        String key = UUIDUtils.getUuId().toUpperCase();
        String offset = key.substring(10, 26);
        String encrypt = EncryptUtil.encryptBySymmetry(text, offset, EncryptUtil.AES);
        // 写入header
        Map<String, String> map = new HashMap<>(2);
        // 模拟请求时的body，key为encrypt，value为实际请求参数的加密结果
        map.put("encrypt", encrypt);
        String body = JSON.toJSONString(map);

        Map<String, String> map2 = new HashMap<>(4);
        map2.put("key", key);
        map2.put("body", body);
        return JSON.toJSONString(map2);
    }

    /**
     * 通过key和加密后的密文，解密为明文
     * 前端请求时，发送的数据已经被加密了；并且加密的偏移量为key(32字符)的10-26个字符共16个字符
     * 前端请求时，发送的数据已经被加密了；并且加密的偏移量为key(32字符)的10-26个字符共16个字符
     *
     * @param text
     * @return
     */
    @GetMapping("/jie")
    public String jie(String key, String text) {
        String offset = key.substring(10, 26);
        return EncryptUtil.decryptBySymmetry(text, offset, EncryptUtil.AES);
    }
}

```

```java
package com.yolo.demo.controller;

import com.yolo.demo.annotation.DecryptRequest;
import com.yolo.demo.annotation.EncryptResponse;
import com.yolo.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
@Slf4j
@EncryptResponse
@DecryptRequest
public class TestController {


    @PostMapping
    public String jia(@RequestBody User user) {
        System.out.println(user);
        return "success";
    }
}

```

## 三、测试

**第一步：**
模拟前端对数据加密，调用`EncryptionController`的`jia`方法，如下

![image-20230627153443235](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230627153443235.png)

**第二步：**
**对body进行加密请求：**

![image-20230627153835454](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230627153835454.png)

**第三步：解密响应参数**

响应给前端解密的key，不会直接在参数中返回，而是写入到`Response Headers`的`heder`中；

![image-20230627154228493](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230627154228493.png)