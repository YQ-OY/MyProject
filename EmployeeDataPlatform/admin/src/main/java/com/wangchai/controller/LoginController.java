package com.wangchai.controller;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.UserInfoDto;
import com.wangchai.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserInfoDto userInfoDto){
        // 登录
        System.out.println("登录");
        return loginService.login(userInfoDto);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        System.out.println("===== 进入LogoutController的logout方法 =====");
        //退出登录
        System.out.println("退出登录");
        return loginService.logout();
    }

}
