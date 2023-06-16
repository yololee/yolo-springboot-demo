package com.yolo.demo.domain;

public class Main {
    public static void main(String[] args) {
        Demo5 demo = Demo5.builder().name("zss").age(20).build();
        System.out.println(demo);
    }
}
