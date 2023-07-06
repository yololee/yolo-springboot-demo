package com.yolo.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Permission {
    /**
    * id
    */
    private Integer pid;

    /**
    * 资源名称
    */
    private String permissionName;

    /**
    * 资源标识符
    */
    private String str;
}