package com.yolo.auto.register.model;


import java.util.List;

public class XxlJobActuatorInfo {

    private Integer recordsFiltered;
    private Integer recordsTotal;
    private List<XxlJobGroup> data;

    public Integer getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Integer recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Integer getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Integer recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public List<XxlJobGroup> getData() {
        return data;
    }

    public void setData(List<XxlJobGroup> data) {
        this.data = data;
    }
}
