package com.example.gamerecom.service;

import cn.hutool.core.collection.CollectionUtil;
import com.example.gamerecom.common.RecommendationUtils;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.dao.Comment_infoDao;
import com.example.gamerecom.dao.Games_infoDao;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.entity.Type_info;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Service
public class GamesInfoService {
    @Resource
    private Games_infoDao games_infoDao;

    @Resource
    private TypeInfoService typeInfoService;
    @Resource
    private Comment_infoDao commentInfoDao;
    @Resource
    private CommentInfoService commentInfoService;
    @Resource
    private CartInfoService cartInfoService;
    @Resource
    private RecommendationUtils recommendationUtils;


    //增加一个游戏详情
    public Games_info add(Games_info games_info){
        //将图片返回游戏对象
        List<Long> fileList = games_info.getFileList();
        if(!CollectionUtil.isEmpty(fileList)){
            games_info.setPictures(fileList.toString());
        }
        games_infoDao.insertSelective(games_info);
        return games_info;
    }

    //delete gamesinfo
    public void delete(Long id){
        games_infoDao.deleteByPrimaryKey(id);
    }

    //更新，修改
    public void update(Games_info games_info){
        List<Long> fileList = games_info.getFileList();
        if(!CollectionUtil.isEmpty(fileList)){
            games_info.setPictures(fileList.toString());
        }
        //点赞hot++
        if (games_info.getHot() != null){
            Games_info games = findById(games_info.getId());
            games_info.setHot(games.getHot() + games_info.getHot());
        }
        games_infoDao.updateByPrimaryKeySelective(games_info);
    }

    //查询
    public Games_info findById(Long id){
        return games_infoDao.selectByPrimaryKey(id);
    }


    //根据原始游戏名模糊查询游戏详情列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    public PageInfo<Games_info> findPage(String name, Integer pageNum,Integer pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Games_info> byName = games_infoDao.findByName(name);
        for (Games_info games_info:byName){
            getRelInfo(games_info);
        }
        return PageInfo.of(byName);

    }

    //根据游戏类型id查询类型名称，加到List中,追加list中没有的数据项
    private void getRelInfo(Games_info games_info){
        if (games_info == null){
            return;
        }
        Type_info type_info = typeInfoService.findById(games_info.getTypeId());
        if (type_info!=null){
            games_info.setTypeName(type_info.getName());
        }
    }

    //根据游戏类型查询最新的数据
    public List<Games_info> findByTypeId(@Param("typeId")Integer typeId){
        return games_infoDao.findByTypeId(typeId);
    }



    //编辑推荐游戏
    public PageInfo<Games_info> findRecommendGames(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Games_info> list = games_infoDao.findRecommendGames();
        return PageInfo.of(list);
    }

    //hot游戏
    public PageInfo<Games_info> findHotGames(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Games_info> list = games_infoDao.findHotGames();
        return PageInfo.of(list);
    }


    //根据关键字搜索game
    public List<Games_info> searchGames(String text) {
        return games_infoDao.searchGames(text);
    }

    //游戏总数
    public  Integer count(){
        return games_infoDao.count();
    }

    //最新游戏
    public PageInfo<Games_info> findDateGames(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Games_info> list = games_infoDao.findDateGames();
        return PageInfo.of(list);
    }

    // 更新游戏评分
    public void updateGameScores() {
        // 获取所有游戏的ID
        List<Games_info> gamesList = games_infoDao.selectAll();
        for (Games_info game : gamesList) {
            Long gameId = game.getId();
            // 计算平均评分
            Double averageRating = commentInfoDao.getAverageRatingByGameId(gameId);
            if (averageRating != null) {
                // 取整处理
                int roundedScore = Math.round(averageRating.floatValue());
                // 更新游戏评分
                game.setScores(roundedScore);
                games_infoDao.updateScoresById(game.getScores(), gameId);
            }
        }
    }

    // 定时任务：每天凌晨 0 点更新游戏评分
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledUpdateGameScores() {
        updateGameScores();
        System.out.println("游戏评分已自动更新");
    }

    //随机推荐
    public List<Games_info> recommendRandom() {
        List<Games_info> allGames = list(); // 获取所有游戏
        if (allGames == null || allGames.isEmpty()) {
            System.err.println("没有找到任何游戏数据");
            return Collections.emptyList();
        }
        List<Games_info> randomGames = RecommendationUtils.random(allGames, 12); // 随机选择12个游戏
        System.out.println("随机推荐的游戏列表: " + randomGames);
        return randomGames;
    }

    public List<Games_info> list() {
        return games_infoDao.selectAll(); // 假设 Mapper 提供了 selectAll 方法
    }

//    public List<Games_info> recommendAction(Long userId) {
//        List<Comment_info> userComments = commentInfoService.getCommentInfoByUserId(userId);
//        if (userComments.isEmpty()) {
//            System.out.println("用户没有评论过游戏，无法生成推荐");
//        }
//        List<Games_info> recommendedGames = recommendationUtils.UBCF(userId, userComments,12);
//        return recommendedGames.subList(0, Math.min(12, recommendedGames.size()));
//    }

}
