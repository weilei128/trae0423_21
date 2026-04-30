package com.library.service.impl;

import com.library.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class InitServiceImpl implements InitService {

    private static final Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);

    private static final String[] TABLES = {"fine_record", "reservation", "borrow_record", "reader", "collection", "book"};

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkDatabaseTables() {
        try {
            for (String table : TABLES) {
                try {
                    jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + table + "` WHERE 1=0", Integer.class);
                } catch (Exception e) {
                    logger.warn("表不存在: " + table);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("检查数据库表失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDatabase(boolean withData) throws Exception {
        logger.info("开始初始化数据库...");
        
        for (String table : TABLES) {
            try {
                jdbcTemplate.execute("DROP TABLE IF EXISTS `" + table + "`");
                logger.info("删除旧表: " + table);
            } catch (Exception e) {
            }
        }

        executeSqlFile("schema.sql");
        logger.info("表结构创建完成");

        if (withData) {
            executeSqlFile("data.sql");
            logger.info("测试数据插入完成");
        }
    }

    private void executeSqlFile(String fileName) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }
                sql.append(line).append(" ");
                
                if (line.endsWith(";")) {
                    String statement = sql.toString().trim();
                    if (!statement.isEmpty()) {
                        try {
                            jdbcTemplate.execute(statement.substring(0, statement.length() - 1));
                        } catch (Exception e) {
                            if (!statement.toLowerCase().contains("drop table")) {
                                logger.error("执行SQL失败: " + statement);
                                throw e;
                            }
                        }
                    }
                    sql.setLength(0);
                }
            }
        }
    }
}
