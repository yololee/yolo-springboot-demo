package com.yolo.xxl.job.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobResponseInfo {

    private Integer code;
    private String msg;
    private String content;
}
