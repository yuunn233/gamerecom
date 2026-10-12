package com.example.gamerecom.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.gamerecom.common.Consts;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.common.ResultCode;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.entity.AuthorityInfo;
import com.example.gamerecom.entity.User_info;
import com.example.gamerecom.exception.CustomException;
import com.example.gamerecom.service.AdminInfoService;
import com.example.gamerecom.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//账号相关的控制器
@RestController
public class AccountController {

    //所有登录用户权限，json格式解析获取权限
    @Value("${authority.info}")
    private String authorityStr;

    @Resource
    private AdminInfoService adminInfoService;
    @Resource
    private UserInfoService userInfoService;


    //通用登录 admin+player
    @PostMapping("/login")
    public Result<Account> login(@RequestBody Account account, HttpServletRequest request){
        if (account==null||StrUtil.isBlank(account.getName())||StrUtil.isBlank(account.getPassword()) || account.getLevel() == null){
            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
        }
        Account login = new Account();
        if (account.getLevel()==1){

            login = adminInfoService.login(account.getName(),account.getPassword());
        }

        if (account.getLevel()==2){

            login = userInfoService.login(account.getName(),account.getPassword());
        }

        request.getSession().setAttribute(Consts.USER,login);
        return Result.success(login);
    }

    // 注册
    @PostMapping("/register")
    public Result<Account> register(@RequestBody Account account) {
        if (account == null || StrUtil.isBlank(account.getName()) || StrUtil.isBlank(account.getPassword()) || account.getLevel() == null) {
            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
        }
        if (!account.getPassword().equals(account.getNewPassword())) {
            throw new CustomException(ResultCode.PARAM_PASSWORD_ERROR);
        }
        Account existingUser = null;
        if (account.getLevel() == 1) {
            existingUser = adminInfoService.findByUserName(account.getName());
        } else if (account.getLevel() == 2) {
            existingUser = userInfoService.findByUserName(account.getName());
        }
        if (existingUser != null) {
            throw new CustomException(ResultCode.USER_ALREADY_EXISTS);
        }
        if (account.getLevel() == 1) {
            Admin_info admin = new Admin_info();
            BeanUtils.copyProperties(account, admin);
            admin.setPassword(SecureUtil.md5(account.getPassword()));
            adminInfoService.add(admin);
        } else if (account.getLevel() == 2) {
            User_info user = new User_info();
            BeanUtils.copyProperties(account, user);
            user.setPassword(SecureUtil.md5(account.getPassword()));
            userInfoService.add(user);
        }
        return Result.success();
    }

    //获取用户登录信息
    @GetMapping("/auth")
    public Result auth(HttpServletRequest request){
        Object user = request.getSession().getAttribute(Consts.USER);
        if(user == null){
            return Result.error("401","未登录");
        }
        return Result.success(user);
    }

    //用户退出
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request){
        request.getSession().setAttribute(Consts.USER,null);
        return Result.success();
    }

    //获取当前用户所能看到的信息
    @GetMapping("/authority")
    public Result<List<Integer>> authority(HttpServletRequest request){
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if (user == null){
            return Result.success(new ArrayList<>());
        }
        JSONArray objects = JSONUtil.parseArray(authorityStr);
        for (Object object:objects){
            JSONObject jsonObject = (JSONObject)object;
            if (user.getLevel().equals((jsonObject.getInt("level")))){
                JSONArray array = JSONUtil.parseArray(jsonObject.getStr("models"));
                List<Integer> modelIdList = array.stream().map(o -> {
                    JSONObject obj = (JSONObject) o;
                    return obj.getInt("modelId");
                }).collect(Collectors.toList());
                return Result.success(modelIdList);
            }
        }
        return Result.success(new ArrayList<>());
    }

    //获取当前模块具有的权限列表
    @GetMapping("/permission/{modelId}")
    public Result<List<Integer>> permission(@PathVariable Integer modelId, HttpServletRequest request){
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if (user == null){
            return Result.success(new ArrayList<>());
        }

        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
        //获取当前用户的level列表
        Optional<AuthorityInfo> optional = authorityInfoList.stream().filter(x -> x.getLevel().equals(user.getLevel())).findFirst();
        if (optional.isPresent()){
            //非空判断
            Optional<AuthorityInfo.Model> firstOption =  optional.get().getModels().stream().filter(x -> x.getModelId().equals(modelId)).findFirst();
            if (firstOption.isPresent()){
                List<Integer> operation = firstOption.get().getOperation();
                return Result.success(operation);
            }
        }
        return Result.success(new ArrayList<>());
    }

    //获取角色列表
    @GetMapping("/getAuthority")
    public Result<List<AuthorityInfo>> getAuthority(){
        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
        return Result.success(authorityInfoList);
    }

    //重置密码为123456  level=1 admin,2 user
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Account account, HttpServletRequest request){
        Integer level = account.getLevel();
        if (level == 1){
            Admin_info info = adminInfoService.findByUserName(account.getName());
            if (info == null){
                return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
            }
            info.setPassword(SecureUtil.md5("123456"));
            adminInfoService.update(info);
        }
        if (level == 2){
            User_info info = userInfoService.findByUserName(account.getName());
            if (info == null){
                return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
            }
//            info.setPassword(SecureUtil.md5("123456"));\
            info.setPassword("123456");
            userInfoService.update(info);
        }
        return Result.success();
    }

    //获取当前用户个人信息
    @GetMapping("/getAccountInfo")
    public Result<Object> getAccountInfo(HttpServletRequest request){
        Account account = (Account)request.getSession().getAttribute(Consts.USER);
        if(account==null){
            return Result.success(new Object());
        }
        Integer level = account.getLevel();
        if(level == 1){
            return Result.success(adminInfoService.findById(account.getId()));
        }
        if(level == 2){
            return Result.success(userInfoService.findById(account.getId()));
        }
        return Result.success(new Object());
    }

    //修改密码
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account info,HttpServletRequest request){
        Account account = (Account)request.getSession().getAttribute(Consts.USER);
        if(account==null){
            return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code,ResultCode.USER_NOT_EXIST_ERROR.msg);
        }
        String oldPassword = info.getPassword();
//        String oldPassword = SecureUtil.md5(info.getPassword());
        if(!oldPassword.equals(account.getPassword())){
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code,ResultCode.PARAM_PASSWORD_ERROR.msg);
        }
//        info.setPassword(SecureUtil.md5(info.getNewPassword()));
        info.setPassword(info.getNewPassword());
        Integer level = account.getLevel();
        if(level == 1){
            Admin_info adminInfo = new Admin_info();
            BeanUtils.copyProperties(info,adminInfo);
            adminInfoService.update(adminInfo);
        }
        if(level == 2){
            User_info userInfo = new User_info();
            BeanUtils.copyProperties(info,userInfo);
            userInfoService.update(userInfo);
        }
        //清空session，让用户重新登录
        request.getSession().setAttribute(Consts.USER,null);
        return Result.success();
    }
}
