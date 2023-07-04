package com.yolo.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.domain.TSysUser;
import com.yolo.demo.mapper.TSysUserMapper;
import com.yolo.demo.service.TSysUserService;
import org.springframework.stereotype.Service;

/**
* @author jujueaoye
* @description 针对表【t_sys_user(系统管理-用户基础信息表)】的数据库操作Service实现
* @createDate 2023-07-03 11:04:06
*/
@Service
public class TSysUserServiceImpl extends ServiceImpl<TSysUserMapper, TSysUser>
    implements TSysUserService{

}




