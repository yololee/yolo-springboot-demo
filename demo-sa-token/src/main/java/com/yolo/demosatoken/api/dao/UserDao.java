package com.yolo.demosatoken.api.dao;


import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demosatoken.api.entity.User;
import com.yolo.demosatoken.api.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {
    public User getOneByLogin(String username, String password) {

        return getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getPassword, SaSecureUtil.sha256(password)));
    }
}
