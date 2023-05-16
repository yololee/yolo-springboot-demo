package com.yolo.xxl.job.http.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "xxl.job.login")
@EnableConfigurationProperties(XxlJobProps.class) //使 使用 @ConfigurationProperties 注解的类生效
public class XxlJobProps {

    private String address;
    private String username;
    private String password;

    private Integer jobGroupId;

}
