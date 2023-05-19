package com.example.druid.modules.system.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 公司单位
* @TableName company
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("company")
public class Company implements Serializable {

    /**
    * id
    */
    @NotNull(message="[id]不能为空")
    private Long id;
    /**
    * 名称
    */
    @Size(max= 255,message="编码长度不能超过255")
    @Length(max= 255,message="编码长度不能超过255")
    private String name;
    /**
    * 联系人
    */
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    private String contact;
    /**
    * 联系方式
    */
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    private String contactType;
    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /**
    * 是否删除(0:存在，-1:删除)
    */
    private Integer removed;
    /**
    * 删除时间
    */
    private LocalDateTime deleteTime;

}
