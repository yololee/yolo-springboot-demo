package com.yolo.demo.domain;

import lombok.Synchronized;

public class Demo9 {
    private Object obj;

    @Synchronized
    public static void hello() {
        System.out.println("world");
    }

    @Synchronized
    public int answerToLife() {
        return 42;
    }

    @Synchronized("obj")
    public void foo() {
        System.out.println("bar");
    }
}
