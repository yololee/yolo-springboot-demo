package com.yolo.multi.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.multi.datasource.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}