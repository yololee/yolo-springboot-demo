package com.yolo.demo.filter;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@WebFilter(filterName = "testFilter", urlPatterns = "/*",
//        initParams = @WebInitParam(name = "noFilterUrl", value = "/test"))
@Component
public class TestFilter implements Filter {
    private List<String> noFilterUrls; 
    
    @Override
    public void init(FilterConfig filterConfig){
        // 从过滤器配置中获取initParams参数
        String noFilterUrl = filterConfig.getInitParameter("noFilterUrl");
        // 将排除的URL放入成员变量noFilterUrls中
        if (StrUtil.isNotBlank(noFilterUrl)) {
            noFilterUrls = new ArrayList<>(Arrays.asList(noFilterUrl.split(",")));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            // 若请求中包含noFilterUrls中的片段则直接跳过过滤器进入下一步请求中
            HttpServletRequest hsr = (HttpServletRequest) servletRequest;
            String s = hsr.getRequestURI();

            boolean b = noFilterUrls.stream().anyMatch(s::contains);
            if (!b) {
                //过滤请求响应逻辑
                System.out.println("执行testFilter111111过滤器具体逻辑");
//                servletRequest = new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
