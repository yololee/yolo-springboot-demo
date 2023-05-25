package com.yolo.validator.common.exception;

import com.yolo.validator.common.exception.core.BaseException;

public class AccessLimitException extends BaseException {

    public AccessLimitException(Integer code, String message) {
        super(code, message);
    }
}
