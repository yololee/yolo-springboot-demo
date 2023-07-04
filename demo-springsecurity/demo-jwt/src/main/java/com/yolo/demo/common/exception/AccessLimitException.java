package com.yolo.demo.common.exception;


import com.yolo.demo.common.exception.core.BaseException;

public class AccessLimitException extends BaseException {

    public AccessLimitException(Integer code, String message) {
        super(code, message);
    }
}
