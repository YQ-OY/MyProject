package com.wangchai.controller;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.EmpEditDto;
import com.wangchai.domain.dto.ForgetPasswordDto;
import com.wangchai.domain.entity.UserInfo;
import com.wangchai.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    // 注册用户
    @PostMapping("/register")
    public ResponseResult register(@RequestBody UserInfo user){
        return userInfoService.register(user);
    }

    // 获取用户信息
    @GetMapping("/{name}")
    public ResponseResult getUserInfo(@PathVariable(value = "name") String name){
        return userInfoService.getUserInfo(name);
    }

    // 修改用户信息
    @PutMapping
    public ResponseResult updateUserInfo(@RequestBody UserInfo userInfo){
        return userInfoService.updateUserInfo(userInfo);
    }

    // 忘记密码
    @PutMapping("/forgetPassword")
    public ResponseResult forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto){
        return userInfoService.forgetPassword(forgetPasswordDto);
    }

}
