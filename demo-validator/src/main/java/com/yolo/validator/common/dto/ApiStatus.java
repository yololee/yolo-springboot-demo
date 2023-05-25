package com.yolo.validator.common.dto;

public enum ApiStatus {
    /**
     * 操作成功
     */
    OK(200, "操作成功"),

    PARAM_ERROR(400, "参数错误"),

    /**
     * 未知异常
     */
    UNKNOWN_ERROR(500, "服务器出错啦");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 内容
     */
    private final String message;

    ApiStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}