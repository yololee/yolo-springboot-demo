package com.yolo.demo.mapper;

import com.yolo.demo.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserRoleMapper {
    //根据id删除
    int deleteByPrimaryKey(Integer id);

   //新增
    int insert(UserRole record);

    //根据id查询
    UserRole selectByPrimaryKey(Integer id);

}