package com.yolo.validator.common.exception.core;

import com.yolo.validator.common.dto.ApiResponse;
import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     *  处理 json 请求体调用接口校验失败抛出的异常
     *  作用于 @Validated @Valid 注解，前端提交的方式为json格式有效，出现异常时会被该异常类处理。
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error("MethodArgumentNotValidException异常，参数校验异常：{}", msg);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,msg);
    }

    /**
     * {@code @Valid}参数校验失败异常
     * 处理 form data方式调用接口校验失败抛出的异常
     * 作用于 @Validated @Valid 注解，仅对于表单提交有效，对于以json格式提交将会失效
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(BindException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");

        log.error("BindException异常，参数校验异常：{}", msg);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,msg);
    }


    /**
     * {@code @Valid}参数校验失败异常
     * 作用于 @NotBlank @NotNull @NotEmpty 注解，校验单个String、Integer、Collection等参数异常处理
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());

        log.error("ConstraintViolationException，参数校验异常：{}", message);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,message);
    }

    /**
     * {@code @Valid}注解使用类型错误
     * 注解 @NotEmpty 用在集合类上面 （不能为null，且Size>0）
     * 注解 @NotBlank 用在String上面 （用于String,不能为null且trim()之后size>0）
     * 注解 @NotNull 用在基本类型上（不能为null，但可以为empty,没有Size的约束）
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(UnexpectedTypeException e) {
        log.error("UnexpectedTypeException，注解使用类型错误", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    /**
     *{@code @Valid}处理自定义注解抛出异常
     */
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ValidationException e){
        log.error("ValidationException，处理自定义注解抛出异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getCause().getMessage());
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
     * 参数绑定异常
     * 注解 @RequestParam 绑定参数错误
     */
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException，参数绑定异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

    @ExceptionHandler(value = ParamException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exceptionHandler(ParamException e) {
        log.error("参数校验失败异常", e);
        return ApiResponse.ofException(ApiStatus.PARAM_ERROR,e.getLocalizedMessage());
    }

}