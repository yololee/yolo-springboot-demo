package com.yolo.demo.service;


import com.yolo.demo.domain.Permission;
import com.yolo.demo.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService{

    @Autowired
    private PermissionMapper permissionMapper;

    
    public int deleteByPrimaryKey(Integer pid) {
        return permissionMapper.deleteByPrimaryKey(pid);
    }

    
    public int insert(Permission record) {
        return permissionMapper.insert(record);
    }

    
    public Permission selectByPrimaryKey(Integer pid) {
        return permissionMapper.selectByPrimaryKey(pid);
    }



}
