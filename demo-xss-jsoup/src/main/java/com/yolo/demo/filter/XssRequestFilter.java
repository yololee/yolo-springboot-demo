package com.yolo.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(filterName = "xssFilter", urlPatterns = {"*.json"})
@Slf4j
@Configuration
public class XssRequestFilter implements Filter {

    private static final List<String> MATCH_WORD = new ArrayList<>();

    static {
        MATCH_WORD.add("SAVE");
        MATCH_WORD.add("UPDATE");
        MATCH_WORD.add("INSERT");
        MATCH_WORD.add("SET");
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest hsr = (HttpServletRequest) request;
            String s = hsr.getRequestURL().toString().toUpperCase();
            //涉及保存操作的进行xss过滤
            boolean b = MATCH_WORD.stream().anyMatch(s::contains);
            if (b) {
                request = new XssHttpServletRequestWrapper((HttpServletRequest) request);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}