package com.yolo.demo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName t_sys_user_role
 */
@TableName(value ="t_sys_user_role")
@Data
public class TSysUserRole implements Serializable {
    private Integer id;

    private Integer roleId;

    private Integer userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}