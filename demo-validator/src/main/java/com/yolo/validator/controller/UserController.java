package com.yolo.validator.controller;

import com.yolo.validator.common.dto.ApiResponse;
import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
import com.yolo.validator.domain.UserParam;
import com.yolo.validator.util.BeanValidatorUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/insert")
    public ApiResponse getUser2(@RequestBody UserParam userParam) {
        Map<String, String> map = BeanValidatorUtil.validateObject(userParam);
        if (MapUtils.isNotEmpty(map)) {
            throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),map.toString());
        }
        return ApiResponse.ofSuccess();
    }
}
