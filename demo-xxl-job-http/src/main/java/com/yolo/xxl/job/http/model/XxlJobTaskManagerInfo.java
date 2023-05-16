package com.yolo.xxl.job.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobTaskManagerInfo {

    private Integer recordsFiltered;
    private Integer recordsTotal;
    private List<XxlJobInfo> data;
}
