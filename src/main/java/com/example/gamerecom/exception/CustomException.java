package com.example.gamerecom.exception;

import com.example.gamerecom.common.ResultCode;

//统一的运行时异常处理
public class CustomException  extends RuntimeException{
    private String code;
    private String msg;


    public CustomException(ResultCode resultCode){
        this.code = resultCode.code;
        this.msg = resultCode.msg;
    }
    public CustomException(String code,String msg){
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
