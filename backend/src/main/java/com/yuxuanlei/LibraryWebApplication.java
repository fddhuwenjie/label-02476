package com.yuxuanlei;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 * <p>
 * 提供 RESTful API 入口，复用现有 MyBatis XML Mapper（{@code resources/mapper/*.xml}）。
 * 数据库连接配置位于 {@code application.yml}。
 */
@SpringBootApplication
@MapperScan("com.yuxuanlei.mapper")
public class LibraryWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(LibraryWebApplication.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("    图书管理系统 Web API 启动中...");
        logger.info("========================================");
        SpringApplication.run(LibraryWebApplication.class, args);
    }
}
