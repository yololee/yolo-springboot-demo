package com.yolo.demo.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

//@ToString
//@EqualsAndHashCode
//@EqualsAndHashCode也有类似的下面的属性,
@ToString(
        includeFieldNames = true, //是否使用字段名
        exclude = {"name"}, //排除某些字段
        of = {"age"}, //只使用某些字段
        callSuper = true //是否让父类字段也参与 默认false
)
public class Demo3 {
    private String name;
    private int age;
}
