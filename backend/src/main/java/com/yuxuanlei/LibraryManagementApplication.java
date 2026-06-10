package com.yuxuanlei;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuxuanlei.mapper")
public class LibraryManagementApplication {

    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApplication.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("    图书管理系统 v1.0 (Spring Boot) 启动中...");
        logger.info("========================================");
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
