package com.example.gamerecom.dao;

import com.example.gamerecom.entity.File_info;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface File_infoDao extends Mapper<File_info> {
    /*根据原始文件名模糊查询文件对象列表*/
    List<File_info> findByName(@Param("name")String name);

}
