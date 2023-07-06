package com.yolo.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    /**
    * 账户id
    */
    private Integer uid;

    /**
    * 姓名
    */
    private String userName;

    /**
    * 密码
    */
    private String password;

    /**
    * 是否可用 1可用 0不可用
    */
    private Integer lock;

    private String salt;

}