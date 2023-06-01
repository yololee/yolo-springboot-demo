package com.yolo.demosatoken.api.service;

import com.yolo.demosatoken.api.dto.AddUserDTO;
import com.yolo.demosatoken.api.dto.LoginDTO;
import com.yolo.demosatoken.common.dto.ApiResponse;

public interface UserService {

    ApiResponse login(LoginDTO loginDTO);

    ApiResponse addUser(AddUserDTO addUserDTO);
}
