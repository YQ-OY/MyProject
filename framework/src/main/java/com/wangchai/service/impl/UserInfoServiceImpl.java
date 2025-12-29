package com.wangchai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.ForgetPasswordDto;
import com.wangchai.domain.entity.UserInfo;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.exception.SystemException;
import com.wangchai.mapper.UserInfoMapper;
import com.wangchai.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult register(UserInfo user) {
        // 对数据进行非空判断
        if(!StringUtils.hasText(user.getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        // 对数据进行是否存在的判断
        // 判断 用户名 是否 存在
        if(userNameExist(user.getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_EXIST);
        }
        // 判断 邮箱 是否 存在
        if(EmailExist(user.getEmail())){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_EXIST);
        }

        // 对密码进行加密
        String encodePasswprd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePasswprd);
        // 然后存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    private boolean userNameExist(String userName) {

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(UserInfo::getName,userName);

        return count(queryWrapper)>0;
    }

    private boolean EmailExist(String Email) {

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(UserInfo::getEmail,Email);

        return count(queryWrapper)>0;
    }

    @Override
    public ResponseResult getUserInfo(String name) {
        UserInfo userInfo = getUserInfoByName(name);
        return ResponseResult.okResult(userInfo);
    }

    // 根据用户名查询用户信息
    private UserInfo getUserInfoByName(String userName){
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getName,userName);
        return getOne(queryWrapper);
    }

    @Override
    public ResponseResult updateUserInfo(UserInfo userInfo) {
        // 对数据进行非空判断
        if(!StringUtils.hasText(userInfo.getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(userInfo.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(userInfo.getEmail())){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        // 对数据进行是否存在的判断
        // 判断 用户名 是否 存在
        if(userNameExist(userInfo.getName()) && !userInfo.getName().equals(getById(userInfo.getUserId()).getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_EXIST);
        }
        // 判断 邮箱 是否 存在
        if(EmailExist(userInfo.getEmail()) && !userInfo.getEmail().equals(getById(userInfo.getUserId()).getEmail())){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_EXIST);
        }

        // 对密码进行加密
        String encodePasswprd = passwordEncoder.encode(userInfo.getPassword());
        userInfo.setPassword(encodePasswprd);

        updateById(userInfo);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult forgetPassword(ForgetPasswordDto forgetPasswordDto) {
        UserInfo userInfoDB = getUserInfoByName(forgetPasswordDto.getName());
        if(userInfoDB == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_EXIST);
        }
        if(!userInfoDB.getEmail().equals(forgetPasswordDto.getEmail())){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_EXIST);
        }
        // 对密码进行加密
        String encodePasswprd = passwordEncoder.encode(forgetPasswordDto.getNewPassword());
        userInfoDB.setPassword(encodePasswprd);
        updateById(userInfoDB);
        return ResponseResult.okResult();
    }


}
