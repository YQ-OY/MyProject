package com.wangchai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wangchai.domain.entity.LoginUser;
import com.wangchai.domain.entity.UserInfo;
import com.wangchai.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Spring Security用户详情服务实现类
 * 负责从数据库获取用户信息，用于认证流程
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getName, username);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        
        // 如果用户不存在，抛出异常
        if (Objects.isNull(userInfo)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        
        // 封装成LoginUser对象返回
        return new LoginUser(userInfo);
    }
}
