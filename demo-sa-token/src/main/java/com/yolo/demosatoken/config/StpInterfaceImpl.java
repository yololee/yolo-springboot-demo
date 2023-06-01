package com.yolo.demosatoken.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demosatoken.api.dao.RoleDao;
import com.yolo.demosatoken.api.dao.UserRoleDao;
import com.yolo.demosatoken.api.entity.Role;
import com.yolo.demosatoken.api.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<>();
//        list.add("101");
//        list.add("user.add");
//        list.add("user.update");
//        list.add("user.get");
        // list.add("user.delete");
//        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();

        List<UserRole> userRoles = userRoleDao.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, loginId));
        if (CollUtil.isNotEmpty(userRoles)){
            List<String> roleIds = userRoles.stream().filter(Objects::nonNull).map(UserRole::getRoleId).collect(Collectors.toList());
            List<Role> roles = roleDao.listByIds(roleIds);
            Set<String> roleNameList = roles.stream().filter(Objects::nonNull).map(Role::getName).collect(Collectors.toSet());
            list.addAll(roleNameList);
        }
        return list;
    }

}
