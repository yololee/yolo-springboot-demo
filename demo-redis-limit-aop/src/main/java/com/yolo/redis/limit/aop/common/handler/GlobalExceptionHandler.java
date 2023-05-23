package com.yolo.redis.limit.aop.common.handler;



import com.yolo.redis.limit.aop.common.dto.ApiResponse;
import com.yolo.redis.limit.aop.common.dto.ApiStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.UnexpectedTypeException;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse exceptionHandler(Exception e) {
        log.error("服务器出现未知错误", e);
        return ApiResponse.ofException(ApiStatus.UNKNOWN_ERROR);
    }


    /**
     * {@code @Valid}参数校验失败异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MethodArgumentNotValidException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,extractError(e.getBindingResult()));
    }

    /**
     * {@code @Valid}参数校验失败异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(BindException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,extractError(e.getBindingResult()));
    }

    /**
     * 无效的参数异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(IllegalArgumentException e) {
        log.error("无效的参数异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    /**
     * {@code @Valid}参数校验失败异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MissingServletRequestParameterException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    /**
     * 无效的参数异常
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(UnexpectedTypeException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    /**
     * 从绑定结果中提出错误字段
     */
    private List<FieldError> extractError(BindingResult bindingResult) {
        List<FieldError> fieldErrors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            FieldError error = new FieldError();
            error.setField(fieldError.getField());
            error.setRejectedValue(fieldError.getRejectedValue());
            error.setDefaultMessage(fieldError.getDefaultMessage());
            fieldErrors.add(error);
        });
        return fieldErrors;
    }

    @Data
    private static class FieldError {

        private String field;
        private Object rejectedValue;
        private String defaultMessage;

    }

}