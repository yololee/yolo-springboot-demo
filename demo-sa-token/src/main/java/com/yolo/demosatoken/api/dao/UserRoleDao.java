package com.yolo.demosatoken.api.dao;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demosatoken.api.entity.UserRole;
import com.yolo.demosatoken.api.mapper.UserRoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {
    public String queryRoleName(Long loginId) {
        return this.baseMapper.queryRoleName(loginId);
    }

    public String queryRoleId(Long loginId) {
        return this.baseMapper.queryRoleId(loginId);
    }
}
