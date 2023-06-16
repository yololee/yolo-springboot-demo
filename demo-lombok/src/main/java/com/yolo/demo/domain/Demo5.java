package com.yolo.demo.domain;

import lombok.Builder;
import lombok.Data;

@Builder
public class Demo5 {
    private final int finalVal = 10;
    private String name;
    private int age;
}
