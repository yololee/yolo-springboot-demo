package com.yolo.demo.easyexcel;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * Excel 导入监听
 */
public interface ExcelListener<T> extends ReadListener<T> {

    ExcelResult<T> getExcelResult();

}
