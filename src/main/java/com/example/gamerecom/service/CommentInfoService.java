package com.example.gamerecom.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.gamerecom.common.Consts;
import com.example.gamerecom.dao.Comment_infoDao;
import com.example.gamerecom.dao.Games_infoDao;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CommentInfoService {
    @Resource
    private Comment_infoDao commentInfoDao;
    @Resource
    private Games_infoDao games_infoDao;

    @Resource
    private AdminInfoService adminInfoService;

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private GamesInfoService gamesInfoService;



    //增加一个评价
    public Comment_info add(Comment_info commentInfo){
        commentInfo.setCommentTime(DateUtil.formatDateTime(new Date()));
        String content = commentInfo.getContent();
        if (content.length()>1000){
            commentInfo.setContent(content.substring(0,1000));
        }
        commentInfoDao.insertSelective(commentInfo);

        // 更新游戏评分
        updateGameScores(commentInfo.getGamesId());

        return commentInfo;
    }

    // 更新游戏评分
    private void updateGameScores(Long gamesId) {
        // 计算平均评分
        Double averageRating = commentInfoDao.getAverageRatingByGameId(gamesId);
        if (averageRating != null) {
            // 取整处理
            int roundedScore = Math.round(averageRating.floatValue());
            // 更新游戏评分
            games_infoDao.updateScoresById(roundedScore, gamesId);
        }
    }

    //delete
    public void delete(Long id){
        commentInfoDao.deleteByPrimaryKey(id);
    }

    //update
    public void update(Comment_info commentInfo){
        String content = commentInfo.getContent();
        if (content.length()>1000){
            commentInfo.setContent(content.substring(0,1000));
        }
        commentInfoDao.updateByPrimaryKeySelective(commentInfo);
    }

    //查询
    public Comment_info findById(Long id){
        return commentInfoDao.selectByPrimaryKey(id);
    }

    //分页查询
    public PageInfo<Comment_info> findPage(Integer pageNum, Integer pageSize,String content, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if(user == null){
            throw new CustomException("1001","session已失效，请重新登录");
        }
        List<Comment_info> infoList;
        PageHelper.startPage(pageNum, pageSize);
        List<Comment_info> list = commentInfoDao.findByContent(content, user.getLevel());
        return PageInfo.of(list);
    }

    //查询所有评价
    public List<Comment_info> findAll(){
        return commentInfoDao.selectAll();
    }


    //查询游戏的评价
    public List<Comment_info> findAll(Long gamesId) {
        List<Comment_info> list = commentInfoDao.findByGamesId(gamesId);
        if (CollectionUtil.isNotEmpty(list)){
            for (Comment_info info : list){
                Integer level = info.getLevel();
                if (level == 1){
                    info.setUserName(adminInfoService.findById(info.getUserId()).getName());
                } else {
                    info.setUserName(userInfoService.findById(info.getUserId()).getName());
                }
            }
        }
        return list;
    }

    public List<Comment_info> list() {
        return commentInfoDao.selectAll();
    }

    public List<Comment_info> getCommentInfoByUserId(Long userId) {
        return commentInfoDao.getCommentInfoByUserId(userId);
    }

    public List<Comment_info> getRatingByGameId(Long gamesId) {
        return  commentInfoDao.getRatingByGameId(gamesId);
    }

    public List<Comment_info> getAllUserRatings() {
        return commentInfoDao.selectAll();
    }
}
