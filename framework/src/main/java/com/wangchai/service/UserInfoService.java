package com.wangchai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.ForgetPasswordDto;
import com.wangchai.domain.entity.UserInfo;

public interface UserInfoService extends IService<UserInfo> {
    ResponseResult register(UserInfo user);

    ResponseResult getUserInfo(String name);

    ResponseResult updateUserInfo(UserInfo userInfo);

    ResponseResult forgetPassword(ForgetPasswordDto forgetPasswordDto);
}
