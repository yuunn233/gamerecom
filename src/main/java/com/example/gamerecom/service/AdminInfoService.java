package com.example.gamerecom.service;

import cn.hutool.crypto.SecureUtil;
import com.example.gamerecom.common.ResultCode;
import com.example.gamerecom.dao.Admin_infoDao;
import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.exception.CustomException;
import com.example.gamerecom.vo.AdminInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminInfoService {
    @Resource
    private Admin_infoDao adminInfoDao;
    //增加一个管理员
    public Admin_info add(Admin_info adminInfo){
        //默认密码
//        adminInfo.setPassword(SecureUtil.md5("123456"));
        adminInfoDao.insertSelective(adminInfo);
        return adminInfo;
    }

    //delete admin
    public void delete(Long id){
        adminInfoDao.deleteByPrimaryKey(id);
    }

    //更新，修改
    public void update(Admin_info adminInfo){
        adminInfoDao.updateByPrimaryKeySelective(adminInfo);
    }

    //查询
    public Admin_info findById(Long id){
        return adminInfoDao.selectByPrimaryKey(id);
    }

    //判断用户名和密码是否正确
    public Admin_info login(String username,String password){

        AdminInfoVo adminInfoVo = adminInfoDao.findByUsername(username);
        //Admin_info adminInfoVo =adminInfoDao.findByUsername(username);
        if (adminInfoVo==null){
            throw  new CustomException(ResultCode.USER_ACCOUNT_ERROR);
        }
//        if (!SecureUtil.md5(password).equals(adminInfoVo.getPassword())){
//            throw  new CustomException(ResultCode.USER_ACCOUNT_ERROR);
//        }
        return adminInfoVo;

    }

    //根据用户名模糊查询admin列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    public PageInfo<AdminInfoVo> findPage(String name, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<AdminInfoVo> byName = adminInfoDao.findByName(name);
        return PageInfo.of(byName);

    }

    //根据用户名查找用户对象，
    public Admin_info findByUserName(String name){
        return  adminInfoDao.findByUsername(name);
    }

    //管理员总数
    public  Integer count(){
        return adminInfoDao.count();

    }
}
