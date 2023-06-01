package com.yolo.demosatoken.common.exception;


import com.yolo.demosatoken.common.dto.ApiStatus;
import com.yolo.demosatoken.common.exception.core.BaseException;

public class ParamException extends BaseException {
    public ParamException(ApiStatus status) {
        super(status);
    }

    public ParamException(Integer code, String message) {
        super(code, message);
    }
}
