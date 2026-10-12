package com.example.gamerecom.dao;

import com.example.gamerecom.entity.Type_info;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface Type_infoDao extends Mapper<Type_info> {
    /*根据类型名模糊查询类型对象列表*/
    List<Type_info> findByName(@Param("name")String name);

    public Integer count();
}
