package com.example.gamerecom.dao;

import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Comment_info;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
public interface Comment_infoDao extends Mapper<Comment_info> {
    //根内容、level查
    List<Comment_info> findByContent(@Param("content")String content,@Param("level")Integer level);


    //查询游戏评价
    List<Comment_info> findByGamesId(@Param("gamesId")Long gamesId);

    @Select("SELECT AVG(rating) FROM comment_info WHERE gamesId = #{gamesId}")
    Double getAverageRatingByGameId(@Param("gamesId") Long gamesId);

    @Select("SELECT * FROM comment_info WHERE userId = #{userId}")
    List<Comment_info> getCommentInfoByUserId(@Param("userId") Long userId);

    @Select("SELECT * from comment_info where gamesId = #{gamesId}")
    List<Comment_info> getRatingByGameId(@Param("gamesId") Long gamesId);

    List<Comment_info> getCommentInfoByUserIdIn(@Param("ratedUserIds")Set<Long> ratedUserIds);

    @Select("SELECT * FROM comment_info WHERE userId = #{otherUser}")
    List<Comment_info> findByUser(@Param("otherUser") Long otherUser);
}
