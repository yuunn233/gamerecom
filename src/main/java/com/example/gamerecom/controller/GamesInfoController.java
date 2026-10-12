package com.example.gamerecom.controller;


import com.example.gamerecom.common.RecommendationUtils;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.service.CartInfoService;
import com.example.gamerecom.service.CommentInfoService;
import com.example.gamerecom.service.GamesInfoService;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/gamesInfo")
public class GamesInfoController {
    @Resource
    private GamesInfoService gamesInfoService;

    @Resource
    private CartInfoService cartInfoService;

    @Resource
    private CommentInfoService commentInfoService;

    @Resource
    private RecommendationUtils recommendationUtils;

    //add type
    @PostMapping
    public Result<Games_info> add(@RequestBody Games_info gamesInfo){
        gamesInfoService.add(gamesInfo);
        return Result.success(gamesInfo);
    }

    //delete type
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        gamesInfoService.delete(id);
        return Result.success();
    }

    //update type
    @PutMapping
    public Result<Games_info> update(@RequestBody Games_info gamesInfo){
        gamesInfoService.update(gamesInfo);
        return Result.success(gamesInfo);
    }

    //find type
    @GetMapping("/{id}")
    public Result<Games_info> findById(@PathVariable Long id){
        Games_info gamesInfo = gamesInfoService.findById(id);
        return Result.success(gamesInfo);
    }


    //根据游戏名模糊查询type列表，分页 name不必选项，游戏名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    @GetMapping("/page/{name}")
    public Result<PageInfo<Games_info>> findPage(@PathVariable String name,@RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "5") Integer pageSize){
        return Result.success(gamesInfoService.findPage(name,pageNum,pageSize));
    }

    //根据游戏类型查询最新的数据
    @GetMapping("/findByTypeId/{typeId}")
    public Result<List<Games_info>> findByTypeId(@PathVariable("typeId")Integer typeId){
        List<Games_info> byTypeId = gamesInfoService.findByTypeId(typeId);
        return Result.success(byTypeId);
    }

    //编辑推荐游戏
    @GetMapping("/recommend")
    public Result<PageInfo<Games_info>> findRecommendGames(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "12") Integer pageSize){
        PageInfo<Games_info> recommends = gamesInfoService.findRecommendGames(pageNum,pageSize);
        return Result.success(recommends);
    }

    //hot游戏
    @GetMapping("/hotgames")
    public Result<PageInfo<Games_info>> findHotGames(@RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "12") Integer pageSize){
        PageInfo<Games_info> hotGames = gamesInfoService.findHotGames(pageNum,pageSize);
        return Result.success(hotGames);
    }

    //根据关键字搜索game
    @GetMapping("/searchGames")
    public Result<List<Games_info>> searchGames(@RequestParam String text){
        return Result.success(gamesInfoService.searchGames(text));
    }

    //最新游戏
    @GetMapping("/dategames")
    public Result<PageInfo<Games_info>> findDateGames(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "12") Integer pageSize){
        PageInfo<Games_info> dateGames = gamesInfoService.findDateGames(pageNum,pageSize);
        return Result.success(dateGames);
    }

    //评分
    @PostMapping("/updateScores")
    public Result updateGameScores() {
        gamesInfoService.updateGameScores();
        return Result.success("游戏评分已更新");
    }


    //随机推荐
    @GetMapping("/recommendRandom")
    public Result<List<Games_info>> recommendRandom() {
        List<Games_info> randomGames = gamesInfoService.recommendRandom();
        return Result.success(randomGames);
    }

    // UBCF 推荐
    @GetMapping("/recommendAction/{id}")
    public Result<List<Games_info>> recommendAction(@PathVariable String id) {
        if (id == null || "undefined".equals(id.trim())) {
            return Result.error("9001", "用户ID无效");
        }
        try {
            Long userId = Long.parseLong(id);
            List<Comment_info> userComments = commentInfoService.getCommentInfoByUserId(userId);
            if (userComments.isEmpty()) {
                return Result.error("9002", "用户没有评论过的游戏，无法生成推荐");
            }
            List<Games_info> recommendedGames = recommendationUtils.UBCF(userId, userComments,12);
            return Result.success(recommendedGames);
        } catch (NumberFormatException e) {
            return Result.error("9002", "用户ID格式不正确");
        }
    }


    // IBCF 推荐
    @GetMapping("/recommendGames/{id}")
    public Result<List<Games_info>> recommendGames(@PathVariable String id) {
        if (id == null || "undefined".equals(id.trim())) {
            return Result.error("9001","用户ID无效");
        }
        try {
            Long userId = Long.parseLong(id);
            List<Comment_info> userComments = commentInfoService.getCommentInfoByUserId(userId);
            if (userComments.isEmpty()) {
                return Result.error("9002", "用户没有评论过的游戏，无法生成推荐");
            }
            List<Games_info> recommendedGames = recommendationUtils.IBCF(userComments,12);
            return Result.success(recommendedGames);
        } catch (NumberFormatException e) {
            return Result.error("9002","用户ID格式不正确");
        }
    }
    //点击的游戏内容推荐
    @GetMapping("/recommendContent/{id}")
    public Result<List<Games_info>> recommendContent(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("9001", "游戏ID无效");
        }
        try {
            Games_info targetGame = gamesInfoService.findById(id); // 获取目标游戏
            if (targetGame == null) {
                return Result.error("9002", "未找到该游戏");
            }
            List<Games_info> allGames = gamesInfoService.list(); // 获取所有游戏
            List<Games_info> recommendedGames = RecommendationUtils.contentBasedRecommendations(allGames, targetGame, 12); // 获取推荐结果
            return Result.success(recommendedGames);
        } catch (Exception e) {
            return Result.error("9003", "推荐失败：" + e.getMessage());
        }
    }

    //内容推荐
    @GetMapping("/recommendCarts/{id}")
    public Result<List<Games_info>> recommendCarts(@PathVariable String id) {
        if (id == null || "undefined".equals(id.trim())) {
            return Result.error("9001", "用户ID无效");
        }
        try {
            Long userId = Long.parseLong(id);
            List<Games_info> allGames = gamesInfoService.list(); // 获取所有游戏
            if (allGames == null || allGames.isEmpty()) {
                System.out.println("没有找到任何游戏数据，无法生成推荐");
            }
            List<Games_info> recommendedGames = recommendationUtils.contentBasedRecommendation(userId, allGames, 12); // 获取推荐结果
            return Result.success(recommendedGames);
        } catch (Exception e) {
            return Result.error("9003", "推荐失败：" + e.getMessage());
        }
    }
}
