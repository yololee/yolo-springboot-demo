package com.yolo.demo.service;

import com.yolo.demo.domain.UserRole;
import com.yolo.demo.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService{

    @Autowired
    private UserRoleMapper userRoleMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return userRoleMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(UserRole record) {
        return userRoleMapper.insert(record);
    }

    
    public UserRole selectByPrimaryKey(Integer id) {
        return userRoleMapper.selectByPrimaryKey(id);
    }



}
