package com.yolo.mybatis.page.controller;

import com.yolo.mybatis.page.dto.RoleDTO;
import com.yolo.mybatis.page.mapper.RoleMapper;
import com.yolo.mybatis.page.page.PageUtils;
import com.yolo.mybatis.page.page.TableDataInfo;
import com.yolo.mybatis.page.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestRoleController extends BaseController{

    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("/page")
    public TableDataInfo page(RoleDTO roleDTO){
        PageUtils.startPage();

        List<Role> roleList = roleMapper.selectPage(roleDTO);

        return PageUtils.getDataTable(roleList);
    }

}
