package com.example.gamerecom.controller;


import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.Type_info;
import com.example.gamerecom.service.TypeInfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/typeInfo")
public class TypeInfoController {
    @Resource
    private TypeInfoService typeInfoService;

    //add type
    @PostMapping
    public Result<Type_info> add(@RequestBody Type_info typeInfo){
        typeInfoService.add(typeInfo);
        return Result.success(typeInfo);
    }

    //delete type
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        typeInfoService.delete(id);
        return Result.success();
    }

    //update type
    @PutMapping
    public Result<Type_info> update(@RequestBody Type_info typeInfo){
        typeInfoService.update(typeInfo);
        return Result.success(typeInfo);
    }

    //find type
    @GetMapping("/{id}")
    public Result<Type_info> findById(@PathVariable Long id){
        Type_info typeInfo = typeInfoService.findById(id);
        return Result.success(typeInfo);
    }


    //根据类型名模糊查询type列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    @GetMapping("/page/{name}")
    public Result<PageInfo<Type_info>> findPage(@PathVariable String name,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "8") Integer pageSize){
        return Result.success(typeInfoService.findPage(name,pageNum,pageSize));
    }

    //查询所有游戏类型
    @GetMapping
    public Result<List<Type_info>> findAll(){
        return Result.success(typeInfoService.findAll());
    }

}
