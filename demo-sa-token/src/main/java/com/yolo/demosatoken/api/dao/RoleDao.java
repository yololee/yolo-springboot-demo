package com.yolo.demosatoken.api.dao;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demosatoken.api.entity.Role;
import com.yolo.demosatoken.api.mapper.RoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDao extends ServiceImpl<RoleMapper, Role> {
}
