package com.example.gamerecom.dao;

import com.example.gamerecom.entity.Games_info;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface Games_infoDao extends Mapper<Games_info> {

    /*根据游戏名称模糊查询列表*/
    List<Games_info> findByName(@Param("name")String name);
    //根据游戏类型查询最新的数据
    List<Games_info> findByTypeId(@Param("typeId")Integer typeId);

    //编辑推荐游戏
    List<Games_info> findRecommendGames();

    //hot游戏
    List<Games_info> findHotGames();

    //根据关键字搜索game
    List<Games_info> searchGames(String text);

    //游戏总数
    public  Integer count();

    //最新游戏
    List<Games_info> findDateGames();

    @Update("UPDATE games_info SET scores = #{scores} WHERE id = #{id}")
    void updateScoresById(@Param("scores") Integer scores, @Param("id") Long id);



}
