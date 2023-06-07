package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BasicEntity {

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

}