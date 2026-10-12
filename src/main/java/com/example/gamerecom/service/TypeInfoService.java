package com.example.gamerecom.service;

import com.example.gamerecom.dao.Type_infoDao;
import com.example.gamerecom.entity.Type_info;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TypeInfoService {
    @Resource
    private Type_infoDao typeInfoDao;
    //增加一个游戏类型
    public Type_info add(Type_info typeInfo){
        typeInfoDao.insertSelective(typeInfo);
        return typeInfo;
    }

    //delete type
    public void delete(Long id){
        typeInfoDao.deleteByPrimaryKey(id);
    }

    //更新，修改
    public void update(Type_info typeInfo){
        typeInfoDao.updateByPrimaryKeySelective(typeInfo);
    }

    //查询
    public Type_info findById(Long id){
        return typeInfoDao.selectByPrimaryKey(id);
    }

    //根据类型名模糊查询游戏类型列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    public PageInfo<Type_info> findPage(String name, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Type_info> byName = typeInfoDao.findByName(name);
        return PageInfo.of(byName);

    }


    //查询所有游戏类型
    public List<Type_info> findAll(){
        return typeInfoDao.selectAll();
    }

    //游戏总数
    public  Integer count(){
        return typeInfoDao.count();
    }
}
