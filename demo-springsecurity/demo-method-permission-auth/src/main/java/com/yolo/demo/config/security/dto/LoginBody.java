package com.yolo.demo.config.security.dto;

import lombok.Data;

@Data
public class LoginBody {

    /**
     * 姓名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
