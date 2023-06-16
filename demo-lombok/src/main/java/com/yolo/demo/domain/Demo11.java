package com.yolo.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class Demo11 {
    private String name;

    //不设置value值，默认是nums的单数(num)；如果nums(只能是复数)随便起名，就会编译错误
    @Singular("num")
    private List<Integer> nums;
}
