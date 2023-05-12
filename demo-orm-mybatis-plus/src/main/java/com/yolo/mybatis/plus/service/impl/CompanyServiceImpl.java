package com.yolo.mybatis.plus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.mybatis.plus.domain.Company;
import com.yolo.mybatis.plus.service.CompanyService;
import com.yolo.mybatis.plus.mapper.CompanyMapper;
import org.springframework.stereotype.Service;

/**
* @author jujueaoye
* @description 针对表【company(公司单位)】的数据库操作Service实现
* @createDate 2023-05-12 10:41:05
*/
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company>
    implements CompanyService{

}




