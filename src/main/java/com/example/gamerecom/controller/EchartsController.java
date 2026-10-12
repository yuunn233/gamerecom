package com.example.gamerecom.controller;

import com.example.gamerecom.common.Result;
import com.example.gamerecom.service.AdminInfoService;
import com.example.gamerecom.service.GamesInfoService;
import com.example.gamerecom.service.TypeInfoService;
import com.example.gamerecom.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Resource
    private AdminInfoService adminInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private GamesInfoService gamesInfoService;

    @Resource
    private TypeInfoService typeInfoService;

    //用户总数
    @GetMapping("/getTotal")
    public Result<Map<String,Object>> getTotal(){
        Map<String,Object> map = new HashMap<>();
        map.put("totalUser",adminInfoService.count() + userInfoService.count());
        map.put("totalGames",gamesInfoService.count());
        map.put("totalType",typeInfoService.count());


        return Result.success(map);
    }

    //
}
