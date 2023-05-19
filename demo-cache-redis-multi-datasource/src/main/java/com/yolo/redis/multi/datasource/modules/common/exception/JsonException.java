package com.yolo.redis.multi.datasource.modules.common.exception;



import com.yolo.redis.multi.datasource.modules.common.dto.ApiStatus;
import lombok.Getter;

/**
 * JSON异常
 */
@Getter
public class JsonException extends BaseException {

    public JsonException(ApiStatus status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}