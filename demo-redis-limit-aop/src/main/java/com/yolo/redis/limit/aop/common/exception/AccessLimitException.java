package com.yolo.redis.limit.aop.common.exception;

public class AccessLimitException extends BaseException {

    public AccessLimitException(Integer code, String message) {
        super(code, message);
    }
}
