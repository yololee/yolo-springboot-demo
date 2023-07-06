package com.yolo.demo.mapper;

import com.yolo.demo.domain.RolePermission;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RolePermissionMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(RolePermission record);


    RolePermission selectByPrimaryKey(Integer id);


    int updateByPrimaryKey(RolePermission record);
}