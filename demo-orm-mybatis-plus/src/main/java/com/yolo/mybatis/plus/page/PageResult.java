package com.yolo.mybatis.plus.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class PageResult {

    private long total; // 总条数
    private List<?> data; // 分页数据
    private long pageNum;
    private long pageSize;

}
