package com.yolo.demo.config.security.service;
import com.yolo.demo.config.security.dto.SecurityUser;
import com.yolo.demo.domain.Permission;
import com.yolo.demo.domain.Role;
import com.yolo.demo.domain.User;
import com.yolo.demo.mapper.PermissionMapper;
import com.yolo.demo.mapper.RoleMapper;
import com.yolo.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义类UserDetailsServiceImpl实现UserDetailsService类 -> 用户认证
 *
 * @author jujueaoye
 * @date 2023/06/30
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    //用户表
    @Autowired
    private UserMapper userMapper;

    //角色表
    @Autowired
    private RoleMapper roleMapper;

    //资源表
    @Autowired
    private PermissionMapper permissionMapper;

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        User user = userMapper.selectBYUserName(username);
        // 判断用户是否存在
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        List<Role> userRoles = roleMapper.selectByUserId(user.getUid());
        for (Role role : userRoles) {
            //添加角色
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            List<Permission> permissionList = permissionMapper.selectByRoleId(role.getRid());
            for (Permission permission : permissionList) {
                //添加权限
                authorities.add(new SimpleGrantedAuthority(permission.getStr()));
            }
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setCurrentUserInfo(user);
        securityUser.setAuthorityList(authorities);
        // 返回UserDetails实现类
        return securityUser;
    }

}