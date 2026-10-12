package com.example.gamerecom.service;

import cn.hutool.crypto.SecureUtil;
import com.example.gamerecom.common.ResultCode;
import com.example.gamerecom.dao.User_infoDao;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.User_info;
import com.example.gamerecom.exception.CustomException;
import com.example.gamerecom.vo.UserInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoService {
    @Resource
    private User_infoDao userInfoDao;
    //增加一个用户
    public User_info add(User_info userInfo){
        //默认密码
//        userInfo.setPassword(SecureUtil.md5());
        userInfoDao.insertSelective(userInfo);
        return userInfo;
    }

    //delete user
    public void delete(Long id){
        userInfoDao.deleteByPrimaryKey(id);
    }

    //更新，修改
    public void update(User_info userInfo){
        userInfoDao.updateByPrimaryKeySelective(userInfo);
    }

    //查询
    public User_info findById(Long id){
        return userInfoDao.selectByPrimaryKey(id);
    }

    //判断用户名和密码是否正确
    public User_info login(String username,String password){

        UserInfoVo userInfoVo = userInfoDao.findByUsername(username);
        //User_info userInfoVo =userInfoDao.findByUsername(username);
        if (userInfoVo==null){
            throw  new CustomException(ResultCode.USER_ACCOUNT_ERROR);
        }
//        if (!SecureUtil.md5(password).equals(userInfoVo.getPassword())){
//            throw  new CustomException(ResultCode.USER_ACCOUNT_ERROR);
//        }
        return userInfoVo;

    }

    //根据用户名模糊查询user列表，分页 name不必选项，用户名，pageNum，查询第几页，pageSize，每页大小，数量，return列表
    public PageInfo<UserInfoVo> findPage(String name, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<UserInfoVo> byName = userInfoDao.findByName(name);
        return PageInfo.of(byName);

    }

    //根据用户名查找用户对象，
    public User_info findByUserName(String name){
        return  userInfoDao.findByUsername(name);
    }

    //用户总数
    public  Integer count(){
        return userInfoDao.count();

    }


}
