package com.yolo.mybatis.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDTO {
    /**
     * 用户名
     */
    private String name;

    /**
     * 手机号码
     */
    private String phoneNumber;
}
