package com.yolo.demo.config.security.dto;

import com.yolo.demo.domain.TSysRole;
import com.yolo.demo.domain.TSysUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义SecurityUser类实现UserDetails类 -> 安全认证用户详情信息
 */
@Data
@Slf4j
public class SecurityUser implements UserDetails {
    /**
     * 当前登录用户
     */
    private transient TSysUser currentUserInfo;

    /**
     * 角色
     */
    private transient List<TSysRole> roleList;

    public SecurityUser() {
    }

    public SecurityUser(TSysUser user) {
        if (user != null) {
            this.currentUserInfo = user;
        }
    }

    public SecurityUser(TSysUser user, List<TSysRole> roleList) {
        if (user != null) {
            this.currentUserInfo = user;
            this.roleList = roleList;
        }
    }


    /**
     * 获取当前用户所具有的角色
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(this.roleList)) {
            for (TSysRole role : this.roleList) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getCode());
                authorities.add(authority);
            }
        }
        return authorities;
    }



    @Override
    public String getPassword() {
        return currentUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return currentUserInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}