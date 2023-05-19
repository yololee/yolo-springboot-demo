package com.example.druid.modules.common.dto;


import lombok.Data;

/**
 * 通用的 API 接口封装
 */
@Data
public class ApiResponse {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回内容
     */
    private String message;

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 无参构造函数
     */
    private ApiResponse() {

    }

    /**
     * 全参构造函数
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     */
    private ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @return ApiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess(Object data) {
        return of(ApiStatus.OK.getCode(), ApiStatus.OK.getMessage(), data);
    }

    /**
     * 构造一个成功且不带数据的API返回
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess() {
        return of(ApiStatus.OK.getCode(), ApiStatus.OK.getMessage(), null);
    }

    /**
     * 构造一个异常且带数据的API返回
     * @return ApiResponse
     */
    public static  ApiResponse ofException(ApiStatus apiStatus, Object data) {
        return of(apiStatus.getCode(), apiStatus.getMessage(), data);
    }

    /**
     * 构造一个异常且不带数据的API返回
     * @return ApiResponse
     */
    public static  ApiResponse ofException(ApiStatus apiStatus) {
        return ofException(apiStatus, null);
    }
}