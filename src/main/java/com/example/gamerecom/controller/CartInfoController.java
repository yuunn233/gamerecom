package com.example.gamerecom.controller;


import com.example.gamerecom.common.Consts;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.exception.CustomException;
import com.example.gamerecom.service.CartInfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/cartInfo")
public class CartInfoController {
    @Resource
    private CartInfoService cartInfoService;

    //add cart
    @PostMapping
    public Result<Cart_info> add(@RequestBody Cart_info cartInfo){
        cartInfoService.add(cartInfo);
        return Result.success(cartInfo);
    }

//    @PostMapping
//    public Result<Cart_info> add(@RequestBody Cart_info cartInfo, HttpServletRequest request) {
//        Account user = (Account) request.getSession().getAttribute(Consts.USER);
//        if(user==null){
//            throw new CustomException("1001","session已失效，请重新登录");
//        }
//        cartInfo.setUserId(user.getId());
//        cartInfoService.add(cartInfo);
//        return Result.success(cartInfo);
//    }

    //delete cart
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        cartInfoService.delete(id);
        return Result.success();
    }


    //find cart
    @GetMapping("/{id}")
    public Result<Cart_info> findById(@PathVariable Long id){
        Cart_info cartInfo = cartInfoService.findById(id);
        return Result.success(cartInfo);
    }


    //分页查询
    @GetMapping("/page")
    public Result<PageInfo<Cart_info>> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "8") Integer pageSize,
                                               HttpServletRequest request){
        return Result.success(cartInfoService.findPage(pageNum,pageSize,request));
    }

    //用户前端分页查询
    @GetMapping("/frontpage")
    public Result<PageInfo<Games_info>> frontpage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "8") Integer pageSize,
                                                  HttpServletRequest request){
        return Result.success(cartInfoService.findFrontPage(pageNum,pageSize,request));
    }
}
