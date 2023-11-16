package com.yolo.demo.util.json;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XxlJobActuatorInfo {

    private Integer recordsFiltered;
    private Integer recordsTotal;
    private List<XxlJobGroup> data;

}
