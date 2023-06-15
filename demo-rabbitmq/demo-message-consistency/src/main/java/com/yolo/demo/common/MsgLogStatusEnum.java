package com.yolo.demo.common;


public enum MsgLogStatusEnum {

    /**
     * 投递中
     **/
    DELIVERING(0, "delivering"),
    /**
     * 投递成功
     **/
    DELIVER_SUCCESS(1, "deliver_success"),
    /**
     * 投递失败
     **/
    DELIVER_FAIL(2, "deliver_fail"),

    /**
     * 消费成功
     **/
    CONSUMED_SUCCESS(3, "consumed_success");


    private final int key;
    private final String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    MsgLogStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
