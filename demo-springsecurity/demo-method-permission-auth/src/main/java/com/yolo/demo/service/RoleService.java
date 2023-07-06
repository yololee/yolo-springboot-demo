package com.yolo.demo.service;

import com.yolo.demo.domain.Role;
import com.yolo.demo.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService{

    @Autowired
    private RoleMapper roleMapper;

    
    public int deleteByPrimaryKey(Integer rid) {
        return roleMapper.deleteByPrimaryKey(rid);
    }

    
    public int insert(Role record) {
        return roleMapper.insert(record);
    }

    
    public Role selectByPrimaryKey(Integer rid) {
        return roleMapper.selectByPrimaryKey(rid);
    }

    


}
