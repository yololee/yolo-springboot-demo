package com.example.druid.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.druid.modules.system.domain.Company;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jujueaoye
* @description 针对表【company(公司单位)】的数据库操作Mapper
* @createDate 2023-05-12 10:41:05
* @Entity com.yolo.mybatis.plus.domain.Company
*/
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {

}




