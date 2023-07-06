package com.yolo.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Role {
    /**
    * id
    */
    private Integer rid;

    /**
    * 角色名称
    */
    private String roleName;
}