package com.example.gamerecom.common;
//所有接口的统一返回格式
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    private Result(T data) {
        this.data = data;
    }

    private Result() {

    }


    //成功的返回，不带对象
    public static Result success(){
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.code);
        result.setMsg(ResultCode.SUCCESS.msg);
        return result;
    }
    //成功的返回，带对象
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>(data);
        result.setCode(ResultCode.SUCCESS.code);
        result.setMsg(ResultCode.SUCCESS.msg);
        return result;
    }
    //失败的返回，不带参数
    public static Result error(){
        Result result = new Result();
        result.setCode(ResultCode.ERROR.code);
        result.setMsg(ResultCode.ERROR.msg);
        return result;
    }
    //失败的返回，带自定义参数
    public static Result error(String code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
