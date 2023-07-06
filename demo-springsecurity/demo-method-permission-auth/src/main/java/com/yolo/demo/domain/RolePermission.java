package com.yolo.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RolePermission {
    /**
    * id
    */
    private Integer id;

    /**
    * 角色id
    */
    private Integer rid;

    /**
    * 权限id
    */
    private Integer pid;
}