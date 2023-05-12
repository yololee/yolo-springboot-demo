package com.yolo.mybatis.plus.mapper;

import com.yolo.mybatis.plus.domain.Company;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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




