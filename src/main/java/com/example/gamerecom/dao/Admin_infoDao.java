package com.example.gamerecom.dao;

import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.vo.AdminInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface Admin_infoDao extends Mapper<Admin_info> {
    //根据用户名查询admin
    AdminInfoVo findByUsername(String username);
    /*根据用户名模糊查询管理员对象列表*/
    List<AdminInfoVo> findByName(@Param("name")String name);
    //管理员总数
    public  Integer count();
}
