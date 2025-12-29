package com.wangchai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.UserInfoDto;
import com.wangchai.domain.entity.LoginUser;
import com.wangchai.domain.entity.UserInfo;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.mapper.UserInfoMapper;
import com.wangchai.service.LoginService;
import com.wangchai.utils.JwtUtil;
import com.wangchai.utils.RedisCache;
import com.wangchai.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResponseResult login(UserInfoDto userInfoDto) {
        try {
            // 封装登录的用户名和密码
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userInfoDto.getName(), userInfoDto.getPassword());

            // 执行认证
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            // 认证成功后获取用户信息
            LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
            String userId = loginUser.getUserInfo().getUserId().toString();

            // 生成JWT token
            String jwt = JwtUtil.createJWT(userId);

            //把loginUser存入Redis。里面有权限信息，后面会用到，设置有效期为12小时
            redisCache.setCacheObject("login:"+userId,loginUser,JwtUtil.JWT_TTL / 1000, TimeUnit.SECONDS);

            // 封装token返回
            Map<String, String> map = new HashMap<>();
            map.put("token", jwt);
            return ResponseResult.okResult(map);

        } catch (AuthenticationException e) {
            // 处理认证失败情况
            if (e instanceof BadCredentialsException) {
                return ResponseResult.errorResult(AppHttpCodeEnum.NAME_OR_PASSWORD_ERROR);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            }
        }
    }

    @Override
    public ResponseResult logout() {
        // 1. 先获取登录用户（修复后，匿名访问时返回null）
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            // 匿名访问：无登录状态，直接返回退出成功
            return ResponseResult.okResult("未登录，无需退出");
        }

        // 2. 已登录：执行退出逻辑（删除Redis中的token）
        Long userId = loginUser.getUserInfo().getUserId();
        redisTemplate.delete("login:" + userId); // 假设你的token存在Redis的key为login:userId

        return ResponseResult.okResult("退出登录成功");
    }
}
