package com.yolo.mybatis.page.page;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }
}