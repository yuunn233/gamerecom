package com.example.gamerecom.dao;

import com.example.gamerecom.entity.User_info;
import com.example.gamerecom.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface User_infoDao extends Mapper<User_info> {
    //根据用户名查询user
    UserInfoVo findByUsername(String username);
    /*根据用户名模糊查询管理员对象列表*/
    List<UserInfoVo> findByName(@Param("name")String name);
    //用户总数
    public  Integer count();
}
