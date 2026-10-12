package com.example.gamerecom.dao;

import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.vo.AdminInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface Cart_infoDao extends Mapper<Cart_info> {
    //根据用户名查询收藏列表
    List<Cart_info> findCartByUserId(@Param("userId")Long userId,@Param("level")Integer level);

    //查询所有的收藏列表
    List<Cart_info> findAll();

//    List<Cart_info> getCartInfoByUserId(Long userId);

    @Select("SELECT * FROM cart_info WHERE userId = #{userId}")
    List<Cart_info> getCartInfoByUserId(@Param("userId") Long userId);

}
