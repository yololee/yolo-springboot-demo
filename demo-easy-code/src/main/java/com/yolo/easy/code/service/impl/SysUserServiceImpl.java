package com.yolo.easy.code.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.easy.code.dao.SysUserDao;
import com.yolo.easy.code.entity.SysUser;
import com.yolo.easy.code.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户信息表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2023-05-11 16:44:36
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

}

