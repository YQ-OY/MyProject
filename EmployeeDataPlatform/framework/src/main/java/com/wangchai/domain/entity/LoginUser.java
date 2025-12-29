package com.wangchai.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {
    private UserInfo userInfo;
    private Collection<? extends GrantedAuthority> authorities;

    // 空参构造
    public LoginUser() {}

    // 仅传入用户信息（无权限）
    public LoginUser(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.authorities = Collections.emptyList(); // 默认空集合，避免NPE
    }

    // 传入用户信息+权限（扩展用）
    public LoginUser(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities) {
        this.userInfo = userInfo;
        // 空值防护：如果权限为null，返回空集合
        this.authorities = authorities == null ? Collections.emptyList() : authorities;
    }

    // ========== UserDetails 接口实现 ==========
    @Override
    public String getUsername() {
        return userInfo != null ? userInfo.getName() : null; // 空指针防护
    }

    @Override
    public String getPassword() {
        return userInfo != null ? userInfo.getPassword() : null; // 空指针防护
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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

    // ========== getter/setter ==========
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities == null ? Collections.emptyList() : authorities;
    }
}