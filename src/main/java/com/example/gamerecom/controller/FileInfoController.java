package com.example.gamerecom.controller;


import cn.hutool.core.io.FileUtil;
import com.example.gamerecom.common.Consts;
import com.example.gamerecom.common.Result;
import com.example.gamerecom.entity.File_info;
import com.example.gamerecom.exception.CustomException;
import com.example.gamerecom.service.FileInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "/files")
public class FileInfoController {

    @Resource
    private FileInfoService fileInfoService;

    //游戏图片上传
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {

        //原始文件名
        String originalName = file.getOriginalFilename();

        //新文件名+时间戳
        String newfileName = cn.hutool.core.io.FileUtil.mainName(originalName)+System.currentTimeMillis()+"."+ FileUtil.extName(originalName);

        //文件上传
        FileUtil.writeBytes(file.getBytes(), Consts.BASE_PATH+newfileName);
        //放入数据库，获取id
        File_info info = new File_info();
        info.setOriginName(originalName);
        info.setFileName(newfileName);
        File_info addInfo = fileInfoService.add(info);
        if (addInfo != null){
            return Result.success(addInfo);
        }else {
            return Result.error("4001","上传失败");
        }
    }

    //download picture
    @GetMapping("/download/{id}")
    public void  download(@PathVariable String id, HttpServletResponse response) throws IOException {
        if (id == null || id.equals("")|| id.equals("null")){
            throw new CustomException("1001","未上传文件");
        }
        File_info file_info = fileInfoService.findById(Long.parseLong(id));
        if (file_info == null){
            throw new CustomException("1001","未查询到该文件");
        }
        byte[] bytes = FileUtil.readBytes(Consts.BASE_PATH+file_info.getFileName());
        response.reset();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(file_info.getOriginName(),"UTF-8"));
        response.addHeader("Content-Length",""+bytes.length);
        response.setContentType("application/octet-stream");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        bufferedOutputStream.write(bytes);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

}
