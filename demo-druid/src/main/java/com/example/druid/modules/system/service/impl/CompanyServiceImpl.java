package com.example.druid.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.druid.modules.system.domain.Company;
import com.example.druid.modules.system.mapper.CompanyMapper;
import com.example.druid.modules.system.service.CompanyService;
import org.springframework.stereotype.Service;

/**
* @author jujueaoye
* @description 针对表【company(公司单位)】的数据库操作Service实现
* @createDate 2023-05-12 10:41:05
*/
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

}




