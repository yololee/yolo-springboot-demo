package com.yolo.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.demo.domain.TSysUser;
import org.springframework.stereotype.Repository;

/**
* @author jujueaoye
* @description 针对表【t_sys_user(系统管理-用户基础信息表)】的数据库操作Mapper
* @createDate 2023-07-03 11:04:06
* @Entity com.yolo.demo.domain.TSysUser
*/
@Repository
public interface TSysUserMapper extends BaseMapper<TSysUser> {

}




