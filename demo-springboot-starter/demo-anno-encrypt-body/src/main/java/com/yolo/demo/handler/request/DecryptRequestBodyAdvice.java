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
