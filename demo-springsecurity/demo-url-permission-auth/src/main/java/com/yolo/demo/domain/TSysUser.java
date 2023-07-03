package com.yolo.demo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName t_sys_user
 */
@TableName(value ="t_sys_user")
@Data
public class TSysUser implements Serializable {
    private Integer id;

    private String username;

    private String password;

    private String nickName;

    private String salt;

    private String token;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}