package com.yolo.demo.task;

import com.yolo.demo.entity.Company;
import com.yolo.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyRunnable {

    @Autowired
    private CompanyService companyService;

    public void batchSave(List<Company> companyList){
        try {
            companyService.saveBatch(companyList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量插入失败");
        }

    }
}
