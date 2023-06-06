package com.yolo.demo.entity;


import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.List;

@Data
public class User {
    private String username;
    private int age;
    private boolean young;

    private List<String> educationList;

}