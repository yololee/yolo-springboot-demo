package com.yolo.demo.controller;

import com.yolo.demo.domain.ExportDemoVo;
import com.yolo.demo.easyexcel.ExcelResult;
import com.yolo.demo.easyexcel.listener.ExportDemoListener;
import com.yolo.demo.utils.ExcelUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class TestController {

    /**
     * 导入表格
     */
    @PostMapping(value = "/read1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ExportDemoVo> importWithOptions(@RequestPart("file") MultipartFile file) throws Exception {
        // 处理解析结果
        ExcelResult<ExportDemoVo> excelResult = ExcelUtil.importExcel(file.getInputStream(), ExportDemoVo.class, new ExportDemoListener());
        return excelResult.getList();
    }
}
