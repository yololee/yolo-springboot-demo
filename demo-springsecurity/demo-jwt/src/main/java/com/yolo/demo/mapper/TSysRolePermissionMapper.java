package com.yolo.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.demo.domain.TSysRolePermission;
import org.springframework.stereotype.Repository;

/**
* @author jujueaoye
* @description 针对表【t_sys_role_permission(系统管理 - 角色-权限资源关联表 )】的数据库操作Mapper
* @createDate 2023-07-03 11:04:06
* @Entity com.yolo.demo.domain.TSysRolePermission
*/
@Repository
public interface TSysRolePermissionMapper extends BaseMapper<TSysRolePermission> {

}




