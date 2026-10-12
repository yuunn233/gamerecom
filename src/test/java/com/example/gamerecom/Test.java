package com.example.gamerecom;

import cn.hutool.crypto.SecureUtil;

public class Test {
    public static void main(String[] args){
        String md5 = SecureUtil.md5("123456");
        System.out.println(md5);
    }
}
