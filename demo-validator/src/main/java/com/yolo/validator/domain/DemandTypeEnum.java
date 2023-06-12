package com.yolo.validator.domain;

import java.util.Arrays;

/**
 * 需求类型枚举
 */
public enum DemandTypeEnum {


    /**
     * 测试
     */
    TEST(0, "测试"),

    /**
     * 正式
     */
    OFFICIAL(1, "正式");


    private final int key;
    private final String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    DemandTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 判断数值是否属于枚举类的值
     */
    public static boolean isInclude(Integer key) {
        return Arrays.stream(values()).anyMatch(e -> key == e.getKey());
    }

    public static String getName(int key) {
        String value = "";
        for (DemandTypeEnum e : values()) {
            if (e.key == key) {
                value = e.getValue();
                break;
            }
        }
        return value;
    }

}
