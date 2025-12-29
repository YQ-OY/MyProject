package com.wangchai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.UserInfoDto;
import com.wangchai.domain.entity.UserInfo;

public interface LoginService extends IService<UserInfo> {
    ResponseResult login(UserInfoDto userInfoDto);


    ResponseResult logout();
}
