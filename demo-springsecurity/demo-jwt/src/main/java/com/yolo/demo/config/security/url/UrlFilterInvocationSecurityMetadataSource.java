package com.yolo.demo.config.security.url;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yolo.demo.config.Constants;
import com.yolo.demo.domain.TSysPermission;
import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysRolePermission;
import com.yolo.demo.mapper.TSysPermissionMapper;
import com.yolo.demo.mapper.TSysRoleMapper;
import com.yolo.demo.mapper.TSysRolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *  获取访问该url所需要的用户角色权限信息
 *
 */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private TSysPermissionMapper tSysPermissionMapper;
    @Autowired
    private TSysRolePermissionMapper tSysRolePermissionMapper;
    @Autowired
    private TSysRoleMapper tSysRoleMapper;

    /***
     * 返回该url所需要的用户权限信息
     *
     * @param object: 储存请求url信息
     * @return: null：标识不需要任何权限都可以访问
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 获取当前请求url
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        // TODO 忽略url请放在此处进行过滤放行
        if ("/login".equals(requestUrl) || requestUrl.contains("logout")) {
            return null;
        }

        // 数据库中所有url
        List<TSysPermission> permissionList = tSysPermissionMapper.selectList(null);
        for (TSysPermission permission : permissionList) {
            // 获取该url所对应的权限
            if (requestUrl.equals(permission.getUrl())) {
                List<TSysRolePermission> permissions = tSysRolePermissionMapper.selectList(Wrappers.<TSysRolePermission>lambdaQuery()
                        .eq(TSysRolePermission::getPermissionId,permission.getId()));
                List<String> roles = new LinkedList<>();
                if (!CollectionUtils.isEmpty(permissions)){
                    Integer roleId = permissions.get(0).getRoleId();
                    TSysRole role = tSysRoleMapper.selectById(roleId);
                    roles.add(role.getCode());
                }
                // 保存该url对应角色权限信息
                return SecurityConfig.createList(roles.toArray(new String[0]));
            }
        }
        // 如果数据中没有找到相应url资源则为非法访问，要求用户登录再进行操作
        return SecurityConfig.createList(Constants.ROLE_LOGIN);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
