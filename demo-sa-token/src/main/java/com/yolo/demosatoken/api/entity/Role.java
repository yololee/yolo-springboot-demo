package com.yolo.demosatoken.api.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("role")
public class Role extends Model<Role> implements Serializable {

    /**
     * 主键id
     */
    @TableId
    private String id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 别名
     */
    private String displayName;
}