package com.yolo.demo.mapper;

import com.yolo.demo.domain.Permission;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface PermissionMapper {
    //根据id删除
    int deleteByPrimaryKey(Integer pid);

    //新增
    int insert(Permission record);

    //根据id查询
    Permission selectByPrimaryKey(Integer pid);

    //根据角色id查询此角色具有的所有权限
    List<Permission> selectByRoleId(Integer rid);
}