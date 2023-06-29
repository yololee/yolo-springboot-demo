package com.yolo.demo.task;

import cn.hutool.core.bean.BeanUtil;
import com.yolo.demo.entity.Company;
import com.yolo.demo.vo.CompanyVO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class QueryTask implements Callable<CompanyVO> {


    private Company company;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public CompanyVO call() {
        CompanyVO vo = new CompanyVO();
        BeanUtil.copyProperties(company,vo);
        return vo;
    }
}
