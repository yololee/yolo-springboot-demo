package com.yolo.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.demo.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
