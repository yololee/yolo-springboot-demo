package com.example.druid.modules.system.controller;



import com.example.druid.modules.common.dto.ApiResponse;
import com.example.druid.modules.system.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 测试api
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class IndexController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/list")
    public ApiResponse list(){
        return ApiResponse.ofSuccess(companyService.list());
    }


}
