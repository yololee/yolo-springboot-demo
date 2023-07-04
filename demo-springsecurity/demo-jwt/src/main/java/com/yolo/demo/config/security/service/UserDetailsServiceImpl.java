package com.yolo.demo.config.security.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysUser;
import com.yolo.demo.domain.TSysUserRole;
import com.yolo.demo.mapper.TSysRoleMapper;
import com.yolo.demo.mapper.TSysUserMapper;
import com.yolo.demo.mapper.TSysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义类UserDetailsServiceImpl实现UserDetailsService类 -> 用户认证
 *
 * @author jujueaoye
 * @date 2023/06/30
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TSysUserMapper tSysUserMapper;

    @Autowired
    private TSysRoleMapper tSysRoleMapper;

    @Autowired
    private TSysUserRoleMapper tSysUserRoleMapper;

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        List<TSysUser> userList = tSysUserMapper.selectList(Wrappers.<TSysUser>lambdaQuery().eq(TSysUser::getUsername, username));
        TSysUser user;
        // 判断用户是否存在
        if (!CollectionUtils.isEmpty(userList)) {
            user = userList.get(0);
        } else {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        return new SecurityUser(user);
    }

    /***
     * 根据token获取用户权限与基本信息
     */
    public SecurityUser getUserByToken(String token) {
        TSysUser user = null;
        List<TSysUser> loginList = tSysUserMapper.selectList(Wrappers.<TSysUser>lambdaQuery().eq(TSysUser::getToken, token));
        if (!CollectionUtils.isEmpty(loginList)) {
            user = loginList.get(0);
        }

        if (ObjectUtil.isNotNull(user)) {
            List<TSysRole> userRoles = getUserRoles(user.getId());
            return new SecurityUser(user, userRoles);
        } else {
            return null;
        }
    }

    /**
     * 根据用户id获取角色权限信息
     *
     * @param userId
     * @return
     */
    private List<TSysRole> getUserRoles(Integer userId) {
        List<TSysUserRole> userRoles = tSysUserRoleMapper.selectList(Wrappers.<TSysUserRole>lambdaQuery().eq(TSysUserRole::getUserId, userId));
        List<TSysRole> roleList = new LinkedList<>();
        for (TSysUserRole userRole : userRoles) {
            TSysRole role = tSysRoleMapper.selectById(userRole.getRoleId());
            roleList.add(role);
        }
        return roleList;
    }

}