package com.yolo.demosatoken.api.vo;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginInfoVO {

    private String tokenHead;
    private String tokenValue;

    private List<String> roleList;
}
