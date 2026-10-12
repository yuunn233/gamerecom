package com.example.gamerecom.controller;

import cn.hutool.core.date.DateUtil;
import com.example.gamerecom.common.Consts;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.service.CommentInfoService;
import com.example.gamerecom.service.GamesInfoService;
import com.github.pagehelper.PageInfo;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.common.ResultCode;
import com.example.gamerecom.exception.CustomException;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//评价controller层

@RestController
@RequestMapping(value = "/commentInfo")
public class CommentInfoController {
    @Resource
    private CommentInfoService commentInfoService;

    @Resource
    private GamesInfoService gamesInfoService;

    //add
    @PostMapping
    public Result<Comment_info> add(@RequestBody Comment_info commentInfo,HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if(user==null){
            throw new CustomException("1001","session已失效，请重新登录");
        }
        commentInfo.setUserId(user.getId());
        commentInfoService.add(commentInfo);
        return Result.success(commentInfo);
    }
//    @PostMapping
//    public Result<Comment_info> add(@RequestBody Comment_info commentInfo) {
//        Comment_info saved = commentInfoService.add(commentInfo);
//        return Result.success(saved);
//    }


    //delete
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        commentInfoService.delete(id);
        return Result.success();
    }

    //update
    @PutMapping
    public Result<Comment_info> update(@RequestBody Comment_info commentInfo) {
        commentInfoService.update(commentInfo);
        return Result.success(commentInfo);
    }

    //根据主键ID查询一个评价
    @GetMapping("/{id}")
    public Result<Comment_info> findById(@PathVariable Long id) {
        Comment_info commentInfo = commentInfoService.findById(id);
        return Result.success(commentInfo);
    }

    //查询所有评价
    @GetMapping
    public Result<List<Comment_info>> findAll(){
        return Result.success(commentInfoService.findAll());
    }

    // 根据游戏ID查询该游戏的所有评价
    @GetMapping("/game/{gamesId}")
    public Result<List<Comment_info>> getCommentsByGame(@PathVariable Long gamesId) {
        return Result.success(commentInfoService.findAll(gamesId));
    }


    //分页查询
    @GetMapping("page/{content}")
    public Result<PageInfo<Comment_info>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @PathVariable String content,
                                               HttpServletRequest request){
        return Result.success(commentInfoService.findPage(pageNum,pageSize,content,request));
    }

}
