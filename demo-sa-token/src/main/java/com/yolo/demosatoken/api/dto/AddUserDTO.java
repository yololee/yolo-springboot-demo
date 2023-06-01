package com.yolo.demosatoken.api.dto;


import lombok.*;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddUserDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 4,max = 20)
    private String username;
    /**
     * 中文名
     */
    @NotBlank(message = "中文名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,}$",message = "必须为俩个字的中文名称")
    private String cnname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 4,max = 20)
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像地址
     */
    private String headUrl;

    /**
     * 角色类型
     */
    @NotBlank(message = "角色类型不能为空")
    private String roleTypeId;

}
