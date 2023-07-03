package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
public class IndexController {

    @GetMapping("/")
    public ModelAndView showHome() {
        return new ModelAndView("home.html");
    }

    @GetMapping("/index")
    public String index() {
        return "Hello World ~";
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    @GetMapping("/home")
    public String home() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("登陆人：" + name);
        return "Hello~ " + name;
    }

    @GetMapping(value ="/admin")
    // 访问路径`/admin` 具有`ADMIN`角色权限   【这种是写死方式】
//    @PreAuthorize("hasPermission('/admin','ADMIN')")
    public String admin() {
        return "Hello~ 管理员";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello~ 测试权限访问接口";
    }
    
}
