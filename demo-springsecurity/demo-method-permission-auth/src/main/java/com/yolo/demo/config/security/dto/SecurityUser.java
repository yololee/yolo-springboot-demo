package com.yolo.demo.config.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yolo.demo.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Collection;


@Component
@Data
public class SecurityUser implements UserDetails {

    /**
     * 当前登录用户
     * 关键字(transient) 在类的实例对象的序列化处理过程中会被忽略
     */
    private transient User currentUserInfo;

    /**
     * 权限
     */
    private transient Collection<GrantedAuthority> authorityList;

    /**
     * 用户的权限集， 默认需要添加ROLE_ 前缀
     *
     * @return {@link Collection}<{@link ?} {@link extends} {@link GrantedAuthority}>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorityList;
    }

    /**
     * 用户的加密后的密码， 不加密会使用{noop}前缀
     *
     * @return {@link String}
     */
    @JsonIgnore
    @Override
    public String getPassword() {
        return currentUserInfo.getPassword();
    }

    /**
     * 用户名
     *
     * @return {@link String}
     */
    @Override
    public String getUsername() {
        return currentUserInfo.getUserName();
    }

    /**
     * 帐户未过期
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE;
    }

    /**
     * 帐户未锁定
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE;
    }

    /**
     * 凭证是否过期
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE;
    }

    /**
     * 用户是否可用
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE;
    }
}
