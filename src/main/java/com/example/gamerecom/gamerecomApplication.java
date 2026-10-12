package com.example.gamerecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.gamerecom.dao")
public class gamerecomApplication {

    public static void main(String[] args) {
        SpringApplication.run(gamerecomApplication.class, args);
    }

}
