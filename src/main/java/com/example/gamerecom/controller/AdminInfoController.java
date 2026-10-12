package com.example.gamerecom.controller;


import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.service.AdminInfoService;
import com.example.gamerecom.vo.AdminInfoVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/adminInfo")
public class AdminInfoController {
    @Resource
    private AdminInfoService adminInfoService;

    //add admin
    @PostMapping
    public Result<Admin_info> add(@RequestBody AdminInfoVo adminInfo){
        adminInfoService.add(adminInfo);
        return Result.success(adminInfo);
    }

    //delete admin
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        adminInfoService.delete(id);
        return Result.success();
    }

    //update admin
    @PutMapping
    public Result<Admin_info> update(@RequestBody AdminInfoVo adminInfo){
        adminInfoService.update(adminInfo);
        return Result.success(adminInfo);
    }

    //find admin
    @GetMapping("/{id}")
    public Result<Admin_info> findById(@PathVariable Long id){
        Admin_info adminInfo = adminInfoService.findById(id);
        return Result.success(adminInfo);
    }


    //根据用户名模糊查询admin列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    @GetMapping("/page/{name}")
    public Result<PageInfo<AdminInfoVo>> findPage(@PathVariable String name,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "8") Integer pageSize){
        return Result.success(adminInfoService.findPage(name,pageNum,pageSize));
    }

}
