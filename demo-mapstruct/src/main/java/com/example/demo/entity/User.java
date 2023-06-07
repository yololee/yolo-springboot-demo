package com.example.demo.entity;

import com.example.demo.dto.PersonDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class User {
    private String username;
    private int age;
    private boolean young;
    private String address;
    private String createTime;
    private BigDecimal source;
    private double height;

    private PersonDto personDto;
}