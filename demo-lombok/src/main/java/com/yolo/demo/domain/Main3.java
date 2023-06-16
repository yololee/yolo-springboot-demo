package com.yolo.demo.domain;

public class Main3 {
    public static void main(String[] args) {
        Demo11 demo = Demo11.builder().name("lucky")
                .num(1).num(2).num(3)
                .build();
        System.out.println("demo = " + demo);
    }
}
