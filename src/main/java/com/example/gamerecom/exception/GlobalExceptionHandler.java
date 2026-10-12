package com.example.gamerecom.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.gamerecom.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//异常统一处理返回
@ControllerAdvice(basePackages = "com.example.gamerecom.controller")
public class GlobalExceptionHandler {

    private  static final Log log = LogFactory.get();

    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json串
    public Result error(Exception e){
        log.error("异常信息",e);
        return Result.error();
    }

    @ExceptionHandler({CustomException.class})
    @ResponseBody //返回json串
    public Result error(CustomException e){
        log.error("自定义异常信息",e.getCode()+" "+e.getMsg());
        return Result.error(e.getCode(),e.getMsg());
    }

}

