package com.yolo.demo.service;

import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.dto.LoginParam;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    ApiResponse selectUserList();

    ApiResponse login(LoginParam loginParam);

    ApiResponse logout(HttpServletRequest request);

    ApiResponse getOnLineCount();

}
