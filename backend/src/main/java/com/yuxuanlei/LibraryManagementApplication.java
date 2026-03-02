package com.yuxuanlei;

import com.yuxuanlei.cli.MainMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图书管理系统启动类
 * @author 俞轩磊
 */
public class LibraryManagementApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApplication.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("    图书管理系统 v1.0 启动中...");
        logger.info("========================================");
        
        try {
            // 启动命令行菜单
            MainMenu mainMenu = new MainMenu();
            mainMenu.show();
        } catch (Exception e) {
            logger.error("系统启动失败", e);
            System.exit(1);
        }
    }
}

