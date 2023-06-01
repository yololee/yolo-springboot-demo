package com.yolo.demosatoken.api.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@TableName("user_role")
public class UserRole extends Model<UserRole> implements Serializable {

    @TableId
    private String userId;
    private String roleId;



}