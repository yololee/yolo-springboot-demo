package com.yolo.demo.domain;

import lombok.Getter;
import lombok.Setter;

// 如果指定在类上,所有字段都会生成get/set方法
// 指定在字段上, 只有标注的字段才会生成get/set方法
@Getter
@Setter
public class Demo2 {
    private String name;
    private int age;
}
