package com.yuxuanlei.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * MyBatis 工具类
 * 支持通过环境变量 DB_HOST 切换数据库连接：
 * - Docker 环境：不设置 DB_HOST，默认连接 mysql:3306
 * - 本地开发：设置 DB_HOST=localhost，连接 localhost:3306
 */
public class MyBatisUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(MyBatisUtil.class);
    private static SqlSessionFactory sqlSessionFactory;
    
    static {
        try {
            Properties props = new Properties();
            props.load(Resources.getResourceAsStream("jdbc.properties"));
            
            // 本地开发时设置 DB_HOST=localhost 覆盖数据库地址
            String dbHost = System.getenv("DB_HOST");
            if (dbHost != null && !dbHost.isEmpty()) {
                props.setProperty("db.host", dbHost);
                logger.info("使用本地数据库: {}", dbHost);
            }
            
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "development", props);
            logger.info("MyBatis SqlSessionFactory 初始化成功");
        } catch (IOException e) {
            logger.error("MyBatis SqlSessionFactory 初始化失败", e);
            throw new RuntimeException("MyBatis 初始化失败", e);
        }
    }
    
    /**
     * 获取 SqlSession
     */
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
    
    /**
     * 获取 SqlSession（自动提交）
     */
    public static SqlSession getSqlSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }
    
    /**
     * 关闭 SqlSession
     */
    public static void closeSqlSession(SqlSession sqlSession) {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }
}
