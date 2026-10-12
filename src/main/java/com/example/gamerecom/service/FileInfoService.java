package com.example.gamerecom.service;

import com.example.gamerecom.dao.File_infoDao;
import com.example.gamerecom.entity.File_info;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileInfoService {
    @Resource
    private File_infoDao file_infoDao;
    //增加一个文件类型
    public File_info add(File_info file_info){
        file_infoDao.insertSelective(file_info);
        return file_info;
    }

    //delete type
    public void delete(Long id){
        file_infoDao.deleteByPrimaryKey(id);
    }

    //更新，修改
    public void update(File_info file_info){
        file_infoDao.updateByPrimaryKeySelective(file_info);
    }

    //查询
    public File_info findById(Long id){
        return file_infoDao.selectByPrimaryKey(id);
    }

    //根据原始文件名模糊查询文件列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    public PageInfo<File_info> findPage(String name, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<File_info> byName = file_infoDao.findByName(name);
        return PageInfo.of(byName);

    }


}
