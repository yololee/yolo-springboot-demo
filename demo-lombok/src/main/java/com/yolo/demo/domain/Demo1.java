package com.yolo.demo.domain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Demo1 {
    @NonNull
    private final int finalVal;
    @NonNull
    private String name;
    @NonNull
    private int age;
}
