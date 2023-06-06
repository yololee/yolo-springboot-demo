package com.yolo.demo.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AutoMapper(target = OrderEntity.class)
public class Order {

    @AutoMapping(dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @AutoMapping(dateFormat = "yyyy_MM_dd HH:mm:ss")
    private Date createTime;

    @AutoMapping(target = "orderDate", dateFormat = "yyyy-MM-dd")
    private String date;

    @AutoMapping(numberFormat = "0.00")
    private BigDecimal orderPrice;

    @AutoMapping(numberFormat = "0.00")
    private Integer goodsNum;

}
