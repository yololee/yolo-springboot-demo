package com.yolo.call.exception;


import com.yolo.call.common.Status;

/**
 * 空指针异常
 */
public class NullPointerException extends BaseException{

    public NullPointerException(Status status) {
        super(status);
    }

    public NullPointerException(Integer code, String message) {
        super(code, message);
    }
}
