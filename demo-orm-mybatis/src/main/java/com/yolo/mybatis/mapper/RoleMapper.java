package com.yolo.mybatis.mapper;

import com.yolo.mybatis.dto.RoleDTO;
import com.yolo.mybatis.dto.UserDTO;
import com.yolo.mybatis.pojo.Role;
import com.yolo.mybatis.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RoleMapper {

    int save(Role role);

    int insertList(@Param("roleList") List<Role> roleList);




    int updateBatchStateById(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    int updateBatch(@Param("roleList")List<Role> roleList);




    List<Role> selectAll();

    Role selectById(@Param("id") Long id);

    List<Role> selectByIds(@Param("ids") List<Integer> ids);

    List<Role> selectPage(RoleDTO roleDTO);




    int deleteById(@Param("id") Long id);

    int batchDelete(@Param("ids") List<Integer> ids);

}
