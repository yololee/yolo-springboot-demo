package com.yolo.demo.mapper;

import com.yolo.demo.domain.Role;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface RoleMapper {
    //根据id删除
    int deleteByPrimaryKey(Integer rid);

    //新增
    int insert(Role record);

   //根据角色id查询
    Role selectByPrimaryKey(Integer rid);

   //根据用户id查询这个用户具有的所有角色
    List<Role> selectByUserId(Integer userid);
}