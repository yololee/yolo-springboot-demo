package com.yolo.demosatoken.api.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.demosatoken.api.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;


@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    String queryRoleName(@Param("userId") Long loginId);

    String queryRoleId(@Param("userId") Long loginId);

    List<String> selectByRoleIds(@Param("roleIds") List<String> roleIds);
}
