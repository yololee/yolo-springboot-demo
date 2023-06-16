package com.yolo.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


//@Accessors(fluent = true)
//@Accessors(chain = true)
@Accessors(fluent = true,chain = true)
//@Accessors(fluent = true,chain = true,prefix = {"xx","yy"})
@Getter
@Setter
@ToString
public class Demo6 {
    private final int finalVal = 10;
    private String xxName;
    private int yyAge;
}
