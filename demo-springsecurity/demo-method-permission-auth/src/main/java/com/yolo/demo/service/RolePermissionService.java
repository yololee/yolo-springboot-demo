package com.yolo.demo.service;

import com.yolo.demo.domain.RolePermission;
import com.yolo.demo.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionService{

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return rolePermissionMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(RolePermission record) {
        return rolePermissionMapper.insert(record);
    }

    
    public RolePermission selectByPrimaryKey(Integer id) {
        return rolePermissionMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKey(RolePermission record) {
        return rolePermissionMapper.updateByPrimaryKey(record);
    }

}
