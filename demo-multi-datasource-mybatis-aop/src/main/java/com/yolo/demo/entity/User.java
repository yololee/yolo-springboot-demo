package com.yolo.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String name;
    private String password;
    private int age;
    private LocalDateTime createTime;

}
