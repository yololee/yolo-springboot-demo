package com.yolo.demo.mapper;

import com.yolo.demo.domain.TSysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author jujueaoye
* @description 针对表【t_sys_user(系统管理-用户基础信息表)】的数据库操作Mapper
* @createDate 2023-06-30 16:36:30
* @Entity com.yolo.demo.domain.TSysUser
*/
@Repository
public interface TSysUserMapper extends BaseMapper<TSysUser> {

}




