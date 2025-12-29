package com.wangchai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("USERINFO")
public class UserInfo {

    @TableId(value = "user_id")
    private Long userId;
    private String name;
    private String password;
    private String photo;
    private String email;
    private String phone;
    private String gender;

}
