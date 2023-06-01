package com.yolo.demosatoken.api.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank
    @Length(min = 4,max = 20)
    private String username;

    /**
     * 密码
     */
    @NotBlank
    @Length(min = 4,max = 20)
    private String password;
}
