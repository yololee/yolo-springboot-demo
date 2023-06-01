package com.yolo.demosatoken.api.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demosatoken.api.dto.AddUserDTO;
import com.yolo.demosatoken.api.dto.LoginDTO;
import com.yolo.demosatoken.api.entity.Role;
import com.yolo.demosatoken.api.entity.User;
import com.yolo.demosatoken.api.entity.UserRole;
import com.yolo.demosatoken.api.service.UserService;
import com.yolo.demosatoken.api.vo.LoginInfoVO;
import com.yolo.demosatoken.common.dto.ApiResponse;
import com.yolo.demosatoken.api.dao.RoleDao;
import com.yolo.demosatoken.api.dao.UserDao;
import com.yolo.demosatoken.api.dao.UserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse login(LoginDTO loginDTO) {

        User user = userDao.getOneByLogin(loginDTO.getUsername(),loginDTO.getPassword());
        Optional.ofNullable(user).orElseThrow(() -> new IllegalArgumentException("用户名或者密码错误"));
        StpUtil.login(user.getId());

        user.setLastLoginTime(LocalDateTime.now());
        userDao.updateById(user);
        LoginInfoVO infoVO = LoginInfoVO.builder().
                tokenHead(StpUtil.getTokenName()).
                tokenValue(StpUtil.getTokenValue()).
                roleList(StpUtil.getRoleList()).build();

        return ApiResponse.ofSuccess(infoVO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse addUser(AddUserDTO addUserDTO) {
        if (checkLoginNameUnique(addUserDTO.getUsername(), false, null)) {
            //校验用户名称是否唯一
            throw new IllegalArgumentException("新增用户'" + addUserDTO.getUsername() + "'失败，登录账号已存在");
        } else if (StrUtil.isNotBlank(addUserDTO.getMobile()) && checkPhoneUnique(addUserDTO.getMobile(), false, null)) {
            //校验手机号码是否唯一
            throw new IllegalArgumentException("新增用户'" + addUserDTO.getUsername() + "'失败，手机号码已存在");
        } else if (StrUtil.isNotBlank(addUserDTO.getEmail()) && checkEmailUnique(addUserDTO.getEmail(), false, null)) {
            //校验邮箱是否唯一
            throw new IllegalArgumentException("新增用户'" + addUserDTO.getUsername() + "'失败，邮箱账号已存在");
        }

        User user = new User();
        BeanUtil.copyProperties(addUserDTO, user);
        user.setPassword(SaSecureUtil.sha256(user.getPassword()));
        userDao.save(user);

        Role role = roleDao.getById(addUserDTO.getRoleTypeId());
        if (ObjectUtil.isNull(role)){
            throw new IllegalArgumentException("新增用户'" + addUserDTO.getUsername() + "'失败，角色类型id不存在");
        }

        userRoleDao.save(UserRole.builder().userId(user.getId()).roleId(role.getId()).build());

        return ApiResponse.ofSuccess(user.getId());
    }

    private boolean checkLoginNameUnique(String username, Boolean excludeMy, String userId) {
        long count = userDao.count(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)
                .ne(excludeMy && StrUtil.isNotBlank(userId),User::getId,userId));
        return count > 0;
    }

    private boolean checkEmailUnique(String email,Boolean excludeMy,String userId) {
        long count = userDao.count(Wrappers.<User>lambdaQuery().eq(User::getEmail, email)
                .ne(excludeMy && StrUtil.isNotBlank(userId),User::getId,userId));
        return count > 0;
    }

    private boolean checkPhoneUnique(String mobile,Boolean excludeMy,String userId) {
        long count = userDao.count(Wrappers.<User>lambdaQuery().eq(User::getMobile, mobile)
                .ne(excludeMy && StrUtil.isNotBlank(userId),User::getId,userId));
        return count > 0;
    }
}
