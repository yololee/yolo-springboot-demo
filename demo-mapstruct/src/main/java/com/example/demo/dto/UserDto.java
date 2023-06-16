package com.example.demo.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserDto {
    private Integer id;
    private String username;
    private int age;
    private boolean young;

    private String address;
    private Date createTime;
    private BigDecimal source;
    private double height;

    private PersonDto personDto;
}