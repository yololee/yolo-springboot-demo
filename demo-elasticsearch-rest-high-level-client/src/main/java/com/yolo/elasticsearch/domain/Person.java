package com.yolo.elasticsearch.domain;

import lombok.Data;

@Data
public class Person {
    private String id;
    private String name;
    private int age;
    private String address;
}
