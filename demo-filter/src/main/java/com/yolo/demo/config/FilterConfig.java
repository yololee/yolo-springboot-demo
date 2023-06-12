package com.yolo.demo.config;


import com.yolo.demo.filter.TestFilter;
import com.yolo.demo.filter.TestFilter2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FilterConfig {

    @Autowired
    private TestFilter testFilter;

    @Autowired
    private TestFilter2 testFilter2;

    @Bean
    public FilterRegistrationBean<?> testFilterRegistration() {
        FilterRegistrationBean<TestFilter> registration = new FilterRegistrationBean<>();
        // 将过滤器配置到FilterRegistrationBean对象中
        registration.setFilter(testFilter);
        // 给过滤器取名
        registration.setName("testFilter");
        // 设置过滤器优先级，该值越小越优先被执行
        registration.setOrder(3);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("noFilterUrl", "/test");
        // 设置initParams参数
        registration.setInitParameters(paramMap);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        // 设置urlPatterns参数
        registration.setUrlPatterns(urlPatterns);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<?> test2FilterRegistration() {
        FilterRegistrationBean<TestFilter2> registration = new FilterRegistrationBean<>();
        // 将过滤器配置到FilterRegistrationBean对象中
        registration.setFilter(testFilter2);
        // 给过滤器取名
        registration.setName("testFilter2");
        // 设置过滤器优先级，该值越小越优先被执行
        registration.setOrder(0);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("noFilterUrl", "/test");
        // 设置initParams参数
        registration.setInitParameters(paramMap);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        // 设置urlPatterns参数
        registration.setUrlPatterns(urlPatterns);
        return registration;
    }
}