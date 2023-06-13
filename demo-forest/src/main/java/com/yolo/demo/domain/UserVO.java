package com.yolo.demo.domain;


import lombok.Data;

@Data
public class UserVO {
    private int id;
    private long userId;
    private double roleId;
    private String name;
    public Integer type;
}
