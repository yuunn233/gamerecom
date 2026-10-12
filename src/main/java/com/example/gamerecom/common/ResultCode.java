package com.example.gamerecom.common;

//所有的返回码和对应文字描述
public enum ResultCode {
    SUCCESS("0","成功"),
    ERROR("-1","系统异常"),
    PARAM_ERROR("1001","参数异常"),
    USER_EXIST_ERROR("2001","用户已存在"),
    USER_ACCOUNT_ERROR("2002","可能是账号，角色或密码错误"),
    USER_NOT_EXIST_ERROR("2003","未找到该用户"),
    PARAM_LOST_ERROR("2004","参数缺失"),
    PARAM_PASSWORD_ERROR("2005","原密码输入错误"),
    USER_ALREADY_EXISTS("2006","账号已存在,无需注册")



    ;

    public String code;
    public String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

