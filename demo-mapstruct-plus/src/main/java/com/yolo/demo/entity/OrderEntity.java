package com.yolo.demo.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.time.LocalDate;

@Data
@AutoMapper(target = Order.class)
public class OrderEntity {

    @AutoMapping(dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String orderTime;

    @AutoMapping(dateFormat = "yyyy_MM_dd HH:mm:ss")
    private String createTime;

    @AutoMapping(target = "date", dateFormat = "yyyy-MM-dd")
    private LocalDate orderDate;

    @AutoMapping(numberFormat = "0.00")
    private String orderPrice;

    @AutoMapping(numberFormat = "0.00")
    private String goodsNum;

}
