package com.yolo.redis.multi.datasource.modules.common.exception;


import com.yolo.redis.multi.datasource.modules.common.dto.ApiStatus;

/**
 * 空指针异常
 */
public class NullPointerException extends BaseException {

    public NullPointerException(ApiStatus status) {
        super(status);
    }

    public NullPointerException(Integer code, String message) {
        super(code, message);
    }
}
