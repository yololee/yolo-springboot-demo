package com.yolo.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.entity.Company;

import com.yolo.demo.mapper.CompanyMapper;
import com.yolo.demo.service.CompanyService;
import org.springframework.stereotype.Service;


@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

}




