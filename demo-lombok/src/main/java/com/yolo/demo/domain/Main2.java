package com.yolo.demo.domain;

public class Main2 {
    public static void main(String[] args) {
        Demo6 demo = new Demo6();
        // setter方法; 这里包含了chain=true的功能,可以链式设置值
        demo.xxName("lucky").yyAge(20);
        // getter方法
        System.out.println(demo.xxName() + "," + demo.yyAge());
        System.out.println("demo = " + demo);
    }
}
