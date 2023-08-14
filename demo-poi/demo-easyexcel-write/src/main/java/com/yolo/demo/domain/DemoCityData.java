package com.yolo.demo.domain;

import lombok.Data;

/**
 * 模拟的数据库省市县
 */
@Data
public class DemoCityData {
    /**
     * 数据库id字段
     */
    private Integer id;
    /**
     * 数据库pid字段
     */
    private Integer pid;
    /**
     * 数据库name字段
     */
    private String name;
    /**
     * MyBatisPlus连带查询父数据
     */
    private DemoCityData pData;

    public DemoCityData(Integer id, Integer pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }
}