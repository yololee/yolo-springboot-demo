package com.yolo.mybatis.page.mapper;


import com.yolo.mybatis.page.dto.RoleDTO;
import com.yolo.mybatis.page.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
