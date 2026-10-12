package com.example.gamerecom.service;

import com.example.gamerecom.dao.Admin_infoDao;
import com.example.gamerecom.dao.User_infoDao;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Admin_info;
import com.example.gamerecom.entity.User_info;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Resource
    private User_infoDao userInfoDao;
    @Resource
    private Admin_infoDao adminInfoDao;


    //查询all
    public List<Account> findAll() {
        // 获取所有普通用户
        List<User_info> users = userInfoDao.selectAll();
        // 获取所有管理员
        List<Admin_info> admins = adminInfoDao.selectAll();

        // 创建一个空的 Account 列表
        List<Account> allAccounts = new ArrayList<>();
        // 将普通用户添加到列表中
        allAccounts.addAll(users.stream()
                .map(user -> (Account) user) // 将 User_info 转换为 Account
                .collect(Collectors.toList()));

        // 将管理员添加到列表中
        allAccounts.addAll(admins.stream()
                .map(admin -> (Account) admin) // 将 Admin_info 转换为 Account
                .collect(Collectors.toList()));

        return allAccounts;
    }
}
