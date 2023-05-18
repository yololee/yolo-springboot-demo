package com.yolo.redis.limit.exception;

public class AccessLimitException extends BaseException {

    public AccessLimitException(Integer code, String message) {
        super(code, message);
    }
}
