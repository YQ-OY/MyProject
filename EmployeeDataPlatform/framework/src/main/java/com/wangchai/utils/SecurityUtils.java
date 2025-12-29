package com.wangchai.utils;

import com.wangchai.domain.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    // 获取登录用户信息（增加类型校验）
    public static LoginUser getLoginUser() {
        try {
            // 获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }
            // 关键：先判断类型，再强转
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
            // 非LoginUser类型（如匿名访问的String），返回null
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取用户ID（兼容getLoginUser返回null的场景）
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        // 匿名访问时，loginUser为null，返回null或0（根据业务调整）
        return loginUser == null ? null : loginUser.getUserInfo().getUserId();
    }
}