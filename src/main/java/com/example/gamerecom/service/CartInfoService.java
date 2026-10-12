package com.example.gamerecom.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.gamerecom.common.Consts;
import com.example.gamerecom.dao.Cart_infoDao;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartInfoService {
    @Resource
    private Cart_infoDao cartInfoDao;

    @Resource
    private AdminInfoService adminInfoService;

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private GamesInfoService gamesInfoService;

    //增加一个收藏
    public Cart_info add(Cart_info cartInfo){
        //不要重复收藏
        Example example = new Example(Cart_info.class);
        example.createCriteria().andEqualTo("userId",cartInfo.getUserId())
                                .andEqualTo("level",cartInfo.getLevel())
                                .andEqualTo("gamesId",cartInfo.getGamesId());
        List<Cart_info> cartInfos = cartInfoDao.selectByExample(example);
//        if (CollectionUtil.isEmpty(cartInfos)){
        cartInfo.setCollectTime(DateUtil.formatDateTime(new Date()));
        cartInfoDao.insertSelective(cartInfo);
//        }
        return cartInfo;
    }


    //delete cart
    public void delete(Long id){
        cartInfoDao.deleteByPrimaryKey(id);
    }


    //查询
    public Cart_info findById(Long id){
        return cartInfoDao.selectByPrimaryKey(id);
    }

    //分页查询
    public PageInfo<Cart_info> findPage(Integer pageNum, Integer pageSize, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if(user == null){
            throw new CustomException("1001","session已失效，请重新登录");
        }
        List<Cart_info> infoList;
        PageHelper.startPage(pageNum, pageSize);
        if(user.getLevel() == 1){
            infoList = cartInfoDao.findAll();
            if(!CollectionUtil.isEmpty(infoList)){
                for(Cart_info cartInfo:infoList){
                    cartInfo.setUserName(adminInfoService.findById(cartInfo.getUserId()).getName());
                }
            }
        }else{
            infoList = cartInfoDao.findCartByUserId(user.getId(),user.getLevel());
            if(!CollectionUtil.isEmpty(infoList)){
                for(Cart_info cartInfo:infoList){
                    cartInfo.setUserName(userInfoService.findById(cartInfo.getUserId()).getName());
                }
            }
        }
        return PageInfo.of(infoList);
    }

    //查询所有收藏
    public List<Cart_info> findAll(){
        return cartInfoDao.selectAll();
    }

    //用户查自己的收藏
    public PageInfo<Games_info> findFrontPage(Integer pageNum, Integer pageSize, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute(Consts.USER);
        if(user == null){
            throw new CustomException("1001","session已失效，请重新登录");
        }
        List<Games_info> gamesInfoList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Cart_info> infoList =cartInfoDao.findCartByUserId(user.getId(),user.getLevel());
        if(CollectionUtil.isNotEmpty(infoList)){
            for(Cart_info cartInfo:infoList){
                Games_info gamesInfo = gamesInfoService.findById(cartInfo.getGamesId());
                if(gamesInfo!=null){
                    gamesInfo.setCartInfoId(cartInfo.getId());
                    gamesInfoList.add(gamesInfo);
                }
            }
        }
        return PageInfo.of(gamesInfoList);
    }

    public List<Cart_info> getCartInfoByUserId(Long userId) {
        return cartInfoDao.getCartInfoByUserId(userId); // 假设 Mapper 提供了相应的方法
    }

}
