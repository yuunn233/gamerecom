package com.example.gamerecom.controller;


import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.User_info;
import com.example.gamerecom.service.UserInfoService;
import com.example.gamerecom.vo.UserInfoVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/userInfo")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    //add user
    @PostMapping
    public Result<User_info> add(@RequestBody UserInfoVo userInfo){
        userInfoService.add(userInfo);
        return Result.success(userInfo);
    }

    //delete user
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        userInfoService.delete(id);
        return Result.success();
    }

    //update user
    @PutMapping
    public Result<User_info> update(@RequestBody UserInfoVo userInfo){
        userInfoService.update(userInfo);
        return Result.success(userInfo);
    }

    //find user
    @GetMapping("/{id}")
    public Result<User_info> findById(@PathVariable Long id){
        User_info userInfo = userInfoService.findById(id);
        return Result.success(userInfo);
    }


    //根据用户名模糊查询user列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    @GetMapping("/page/{name}")
    public Result<PageInfo<UserInfoVo>> findPage(@PathVariable String name,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "8") Integer pageSize){
        return Result.success(userInfoService.findPage(name,pageNum,pageSize));
    }

}
