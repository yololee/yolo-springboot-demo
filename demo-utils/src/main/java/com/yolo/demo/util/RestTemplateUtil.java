package com.yolo.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

/**
 * @author zb
 * @date 2021-01-06 15:41
 **/
@Slf4j
@Component
public class RestTemplateUtil {

    @Resource
    private RestTemplate restTemplate;

    private static final String contentType = "Content-Type";

    /**
     * HTTP GET，返回给定类型数据
     *
     * @param url      请求地址
     * @param respType 返回值类型
     * @param <T>      Entity
     * @return Entity
     */
    public <T> T doGet(String url, Class<T> respType) {
        return doGet(url, null, respType);
    }

    /**
     * HTTP GET，返回给定类型数据
     *
     * @param url      请求地址
     * @param respType 返回值类型
     * @param <T>      泛型
     * @return 泛型返回值
     */
    public <T> T doGet(String url, ParameterizedTypeReference<T> respType) {
        return doGet(url, null, respType);
    }

    /**
     * HTTP GET，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doGet(String url, Map<String, String> headerMap, Class<T> respType) {
        return baseGet(url, headerMap, respType, null);
    }

    /**
     * HTTP GET，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doGet(String url, Map<String, String> headerMap, ParameterizedTypeReference<T> respType) {
        return baseGet(url, headerMap, null, respType);
    }

    /**
     * HTTP GET，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    private <T> T baseGet(String url, Map<String, String> headerMap, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 请求参数体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, createHttpHeaders(headerMap));
        // 响应参数体
        ResponseEntity<T> responseEntity;
        if (classType != null) {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, classType);
        } else {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, respType);
        }
        log.debug("请求完成！请求状态码为：【{}】，请求响应内容为：【{}】", responseEntity.getStatusCodeValue(), responseEntity.getBody());
        return responseEntity.getBody();
    }

    /**
     * 创建HttpHeaders对象
     *
     * @param headerMap 请求头参数
     * @return HttpHeaders
     */
    public HttpHeaders createHttpHeaders(Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(headerMap)) {
            for (String key : headerMap.keySet()) {
                headers.set(key, headerMap.get(key));
            }
        }
        return headers;
    }

    /**
     * HTTP POST，返回给定类型数据
     *
     * @param url      请求地址
     * @param jsonData 请求参数-Json结构
     * @param respType 返回值类型
     * @param <T>      泛型
     * @return 泛型返回值
     */
    public <T> T doPost(String url, String jsonData, Class<T> respType) {
        return doPost(url, null, jsonData, respType);
    }

    /**
     * HTTP POST，返回给定类型数据
     *
     * @param url      请求地址
     * @param jsonData 请求参数-Json结构
     * @param respType 返回值类型
     * @param <T>      泛型
     * @return 泛型返回值
     */
    public <T> T doPost(String url, String jsonData, ParameterizedTypeReference<T> respType) {
        return doPost(url, null, jsonData, respType);
    }

    /**
     * HTTP POST，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param jsonData  请求参数-Json结构
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doPost(String url, Map<String, String> headerMap, String jsonData, ParameterizedTypeReference<T> respType) {
        return basePost(url, headerMap, jsonData, null, respType);
    }

    /**
     * HTTP POST，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param jsonData  请求参数-Json结构
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doPost(String url, Map<String, String> headerMap, String jsonData, Class<T> respType) {
        return basePost(url, headerMap, jsonData, respType, null);
    }

    /**
     * HTTP POST，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param jsonData  请求参数-Json结构
     * @param classType 返回值类型
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    private <T> T basePost(String url, Map<String, String> headerMap, String jsonData, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 请求头
        HttpHeaders httpHeaders = createHttpHeaders(headerMap);
        // 设置默认的 Content-Type
        if (!httpHeaders.containsKey(contentType)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        // 请求参数体
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, httpHeaders);
        return baseRequest(url, HttpMethod.POST, requestEntity, classType, respType);
    }

    /**
     * HTTP FormData，返回给定类型数据
     *
     * @param url       请求地址
     * @param paramMap  Form Data (application/x-www-form-urlencoded)
     * @param classType 请求类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doFormData(String url, MultiValueMap<String, Object> paramMap, Class<T> classType) {
        return doFormData(url, null, paramMap, classType);
    }

    /**
     * HTTP FormData，返回给定类型数据
     *
     * @param url      请求地址
     * @param paramMap 请求参数 (application/x-www-form-urlencoded)
     * @param respType 请求类型
     * @param <T>      泛型
     * @return 泛型返回值
     */
    public <T> T doFormData(String url, MultiValueMap<String, Object> paramMap, ParameterizedTypeReference<T> respType) {
        return doFormData(url, null, paramMap, respType);
    }

    /**
     * HTTP FormData，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param paramMap  请求参数 (application/x-www-form-urlencoded)
     * @param classType 返回值类型
     * @param <T>       泛型型
     * @return 泛型返回值
     */
    public <T> T doFormData(String url, Map<String, String> headerMap, MultiValueMap<String, Object> paramMap, Class<T> classType) {
        return baseFormData(url, headerMap, paramMap, classType, null);
    }

    /**
     * HTTP FormData，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头
     * @param paramMap  请求参数 (application/x-www-form-urlencoded)
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    public <T> T doFormData(String url, Map<String, String> headerMap, MultiValueMap<String, Object> paramMap, ParameterizedTypeReference<T> respType) {
        return baseFormData(url, headerMap, paramMap, null, respType);
    }

    /**
     * @param url       请求地址
     * @param headerMap 请求头数据
     * @param paramMap  请求参数
     * @param classType 返回值类型
     * @param respType  返回值类型
     * @param <T>       返回值泛型
     * @return 返回值
     */
    private <T> T baseFormData(String url, Map<String, String> headerMap, MultiValueMap<String, Object> paramMap, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 请求头
        HttpHeaders httpHeaders = createHttpHeaders(headerMap);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 请求参数体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, httpHeaders);
        return baseRequest(url, HttpMethod.POST, requestEntity, classType, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url        请求地址
     * @param httpMethod 请求类型
     * @param jsonData   请求参数-Json结构
     * @param respType   返回值类型
     * @param <T>        泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, String jsonData, Class<T> respType) {
        return generalRequest(url, httpMethod, Collections.emptyMap(), jsonData, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url        请求地址
     * @param httpMethod 请求
     * @param jsonData   请求参数-Json结构
     * @param respType   返回值类型
     * @param <T>        泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, String jsonData, ParameterizedTypeReference<T> respType) {
        return generalRequest(url, httpMethod, Collections.emptyMap(), jsonData, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url        请求地址
     * @param httpMethod 请求类型
     * @param headerMap  请求头参数
     * @param jsonData   请求参数-Json结构
     * @param respType   返回值类型
     * @param <T>        泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, Map<String, String> headerMap, String jsonData, ParameterizedTypeReference<T> respType) {
        return baseRequest(url, httpMethod, headerMap, jsonData, null, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url        请求地址
     * @param httpMethod 请求类型
     * @param headerMap  请求头参数
     * @param jsonData   请求参数-Json结构
     * @param respType   返回值类型
     * @param <T>        泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, Map<String, String> headerMap, String jsonData, Class<T> respType) {
        return baseRequest(url, httpMethod, headerMap, jsonData, respType, null);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url         请求地址
     * @param httpMethod  请求类型
     * @param httpHeaders 请求头参数
     * @param jsonData    请求参数-Json结构
     * @param respType    返回值类型
     * @param <T>         泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, String jsonData, ParameterizedTypeReference<T> respType) {
        return baseRequest(url, httpMethod, httpHeaders, jsonData, null, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url         请求地址
     * @param httpMethod  请求类型
     * @param httpHeaders 请求头参数
     * @param jsonData    请求参数-Json结构
     * @param respType    返回值类型
     * @param <T>         泛型
     * @return 泛型返回值
     */
    public <T> T generalRequest(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, String jsonData, Class<T> respType) {
        return baseRequest(url, httpMethod, httpHeaders, jsonData, respType, null);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url       请求地址
     * @param headerMap 请求头参数
     * @param jsonData  请求参数-Json结构
     * @param classType 返回值类型
     * @param respType  返回值类型
     * @param <T>       泛型
     * @return 泛型返回值
     */
    private <T> T baseRequest(String url, HttpMethod httpMethod, Map<String, String> headerMap, String jsonData, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 请求头
        HttpHeaders httpHeaders = createHttpHeaders(headerMap);
        // 设置默认的 Content-Type
        if (!httpHeaders.containsKey(contentType)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        // 请求参数体
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, httpHeaders);
        return baseRequest(url, httpMethod, requestEntity, classType, respType);
    }

    /**
     * 自定义请求类型，返回给定类型数据
     *
     * @param url         请求地址
     * @param httpHeaders 请求头参数
     * @param jsonData    请求参数-Json结构
     * @param classType   返回值类型
     * @param respType    返回值类型
     * @param <T>         泛型
     * @return 泛型返回值
     */
    private <T> T baseRequest(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, String jsonData, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 请求参数体
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, httpHeaders);
        return baseRequest(url, httpMethod, requestEntity, classType, respType);
    }

    /**
     * @param url           请求地址
     * @param httpMethod    Http请求方法
     * @param requestEntity 请求参数体
     * @param classType     返回值类型
     * @param respType      返回值类型
     * @param <T>           返回值泛型
     * @return 返回值
     */
    private <T> T baseRequest(String url, HttpMethod httpMethod, HttpEntity<?> requestEntity, Class<T> classType, ParameterizedTypeReference<T> respType) {
        // 返回值响应体
        log.info("请求开始! 请求url为:【{}】,请求体为:【{}】", url, requestEntity.getBody());
        ResponseEntity<T> responseEntity;
        if (respType != null) {
            responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, respType);
        } else {
            responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, classType);
        }
        log.info("请求完成！请求状态码为：【{}】，请求响应内容为：【{}】", responseEntity.getStatusCodeValue(), responseEntity.getBody());
        return responseEntity.getBody();
    }

}
