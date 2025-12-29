package com.wangchai.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    FILE_IS_EMPTY(501,"请选择要上传的文件"),
    FILE_SIZE_EXCEED(502,"文件大小不能超过2MB"),
    FILE_NAME_INVALID(503, "仅支持上传png、jpg、jpeg、webp格式的图片"),
    EMP_ID_EXIST(504, "员工ID已存在"),
    EMP_NOT_EXIST(505,"员工不存在"),
    USERNAME_NOT_NULL(518,"用户名不能为空"),
    PASSWORD_NOT_NULL(519,"密码不能为空"),
    EMAIL_NOT_NULL(520,"邮箱不能为空"),
    USERNAME_EXIST(521,"用户名已存在"),
    EMAIL_EXIST(522,"邮箱已存在"),
    USER_NOT_EXIST(523,"用户不存在"),
    DEPT_ID_EXIST(524,"部门编号已存在"),
    DEPT_NOT_EXIST(525,"部门不存在"),
    DEPT_HAS_EMP(526,"部门下有员工，不能删除"),
    SALARY_ID_EXIST(527,"薪资编号已存在"),
    SALARY_NOT_EXIST(528,"薪资不存在"),
    EMAIL_NOT_EXIST(529,"邮箱不存在"),
    NAME_OR_PASSWORD_ERROR(530,"用户名或密码错误");



    int code;
    String msg;
    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}

