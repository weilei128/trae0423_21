package com.library.config;

import com.library.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private InitService initService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            if (!initService.checkDatabaseTables()) {
                logger.info("数据库表不存在，开始自动初始化...");
                initService.initDatabase(true);
                logger.info("数据库初始化完成！已创建表结构并插入测试数据。");
            } else {
                logger.info("数据库表已存在，跳过初始化。");
            }
        } catch (Exception e) {
            logger.error("数据库初始化失败: " + e.getMessage(), e);
        }
    }
}
