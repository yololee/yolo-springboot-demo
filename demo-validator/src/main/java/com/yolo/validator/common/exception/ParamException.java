package com.yolo.validator.common.exception;

import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.core.BaseException;

public class ParamException extends BaseException {
    public ParamException(ApiStatus status) {
        super(status);
    }

    public ParamException(Integer code, String message) {
        super(code, message);
    }
}
