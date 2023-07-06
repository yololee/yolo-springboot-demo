package com.yolo.demo.common.exception;


import com.yolo.demo.common.dto.ApiStatus;
import com.yolo.demo.common.exception.core.BaseException;

public class ParamException extends BaseException {
    public ParamException(ApiStatus status) {
        super(status);
    }

    public ParamException(Integer code, String message) {
        super(code, message);
    }
}
