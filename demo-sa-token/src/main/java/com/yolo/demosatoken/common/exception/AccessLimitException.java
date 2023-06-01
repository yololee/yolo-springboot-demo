package com.yolo.demosatoken.common.exception;


import com.yolo.demosatoken.common.exception.core.BaseException;

public class AccessLimitException extends BaseException {

    public AccessLimitException(Integer code, String message) {
        super(code, message);
    }
}
