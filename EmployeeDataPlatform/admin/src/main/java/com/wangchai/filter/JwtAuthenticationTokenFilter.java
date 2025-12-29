package com.wangchai.filter;

import com.wangchai.domain.entity.LoginUser;
import com.wangchai.service.UserInfoService;
import com.wangchai.utils.JwtUtil;
import com.wangchai.utils.RedisCache;
import com.wangchai.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;
    // 移除对UserInfoService的直接依赖（过滤器只做token解析+Redis读取）

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 定义需要跳过JWT校验的接口路径（和Security配置保持一致）
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        // 匹配/login和/forgetPassword接口，直接放行，不解析token
        if (pathMatcher.match("/login", requestURI) || pathMatcher.match("/userInfo/forgetPassword", requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析token获取userId（仅做解析，不查数据库）
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Long userId = Long.valueOf(claims.getSubject());

        // 从Redis读取LoginUser（登录时已存入，无需查库）
        LoginUser loginUser = (LoginUser) redisCache.getCacheObject("login:" + userId);
        if (loginUser == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 构建认证对象并写入SecurityContext
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser, null, loginUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}