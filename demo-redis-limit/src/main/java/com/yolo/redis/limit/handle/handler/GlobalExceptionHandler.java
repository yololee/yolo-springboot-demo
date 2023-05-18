package com.yolo.redis.limit.handle.handler;

import com.yolo.redis.limit.common.ApiStatus;
import com.yolo.redis.limit.exception.AccessLimitException;
import com.yolo.redis.limit.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 统一 AccessLimit 异常处理
     *
     * @param exception JsonException
     * @return 统一返回 json 格式
     */
    @ExceptionHandler(value = AccessLimitException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse accessLimitExceptionHandler(AccessLimitException exception) {
        log.error("【AccessLimit】:{}", exception.getMessage());
        return ApiResponse.ofException(ApiStatus.UNKNOWN_ERROR,exception.getLocalizedMessage());
    }

    /**
     * 全局异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse exceptionHandler(Exception e) {
        log.error("服务器出现未知错误", e);
        return ApiResponse.ofException(ApiStatus.UNKNOWN_ERROR,e.getLocalizedMessage());
    }


}