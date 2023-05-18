package com.yolo.exception.handler.exception;

import com.yolo.exception.handler.common.ApiStatus;

/**
 * 空指针异常
 */
public class NullPointerException extends BaseException{

    public NullPointerException(ApiStatus status) {
        super(status);
    }

    public NullPointerException(Integer code, String message) {
        super(code, message);
    }
}
