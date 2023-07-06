package com.yolo.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRole {
    /**
    * id
    */
    private Integer id;

    /**
    * 用户id
    */
    private Integer uid;

    /**
    * 角色id
    */
    private Integer rid;
}