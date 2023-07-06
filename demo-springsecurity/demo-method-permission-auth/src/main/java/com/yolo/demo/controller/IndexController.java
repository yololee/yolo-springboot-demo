package com.yolo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
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

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login.html");
    }

    @GetMapping("/home")
    public String home() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("登陆人：" + name);
        return "Hello~ " + name;
    }

    @GetMapping("/cs")
    @PreAuthorize("hasAnyAuthority('sys:user:delete')")
    public String cs(){
        System.out.println("说明你具有delete权限");
        return "说明你具有delete权限";
    }

    @GetMapping("/test1")
    @PreAuthorize("hasRole('test')")
    public String test1(){
        System.out.println("说明你具有ROLE_admin角色");
        return "说明你具有ROLE_admin角色";
    }


    @GetMapping("/test")
    @Secured({"ROLE_normal","ROLE_admin"})
    public String test() {
        return "Hello~ 测试权限访问接口";
    }
    
}
