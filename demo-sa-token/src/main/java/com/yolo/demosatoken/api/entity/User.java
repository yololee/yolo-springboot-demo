package com.yolo.demosatoken.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Accessors(chain = true)
@TableName("user")
public class User extends Model<User> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId
    private String id;
    /**
     * username
     */
    private String username;
    /**
     * cnname
     */
    private String cnname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 密码
     */
    @JsonIgnore
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String headUrl;

    /**
     * createTime
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * updateTime
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 客户id
     */
    private Long customerId;
}
