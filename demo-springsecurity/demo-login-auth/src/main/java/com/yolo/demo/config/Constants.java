package com.yolo.demo.config;


public class Constants {
    /**
     * 密码加密相关
     */
    public static String SALT = "yolo";
    public static final int HASH_ITERATIONS = 256;

    /**
     * 请求头类型：
     * application/x-www-form-urlencoded ： form表单格式
     * application/json ： json格式
     */
    public static final String REQUEST_HEADERS_CONTENT_TYPE = "application/json";
}
