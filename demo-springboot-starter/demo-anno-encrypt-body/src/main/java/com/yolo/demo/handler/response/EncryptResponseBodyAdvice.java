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
