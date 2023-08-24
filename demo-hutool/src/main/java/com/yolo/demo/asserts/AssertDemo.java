package com.yolo.demo.asserts;

import cn.hutool.core.lang.Assert;

public class AssertDemo {
    public static void main(String[] args) {
        Boolean aFalse = Boolean.FALSE;
//        Assert.isTrue(aFalse, () -> new IllegalArgumentException("参数为false"));

        Assert.isTrue(aFalse,"参数为flase");
    }
}
