package com.yolo.demo.domain;

public enum UserStatus {

    /**
     * 正常
     */
    OK("0", "正常"),
    DISABLE("1", "停用"),
    DELETED("2", "删除");

    private final String code;
    private final String info;

    UserStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}