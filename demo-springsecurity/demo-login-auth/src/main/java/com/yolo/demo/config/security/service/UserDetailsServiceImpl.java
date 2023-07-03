package com.yolo.demo.config.security.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.domain.TSysUser;
import com.yolo.demo.mapper.TSysUserMapper;
import com.yolo.demo.config.security.dto.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        List<TSysUser> userList = tSysUserMapper.selectList(Wrappers.<TSysUser>lambdaQuery().eq(TSysUser::getUsername,username));
        TSysUser user;
        // 判断用户是否存在
        if (!CollectionUtils.isEmpty(userList)){
            user = userList.get(0);
        } else {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        return new SecurityUser(user);
    }
}