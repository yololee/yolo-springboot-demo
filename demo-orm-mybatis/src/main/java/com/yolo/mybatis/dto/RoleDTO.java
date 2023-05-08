package com.yolo.mybatis.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoleDTO {
    private String roleName;
    private String status;
}
