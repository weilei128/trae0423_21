package com.library.controller;

import com.library.common.Result;
import com.library.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private InitService initService;

    @GetMapping("/check")
    public Result<Boolean> checkDatabase() {
        boolean exists = initService.checkDatabaseTables();
        return Result.success(exists);
    }

    @GetMapping("/database")
    public Result<String> initDatabaseGet(@RequestParam(required = false, defaultValue = "true") boolean withData) {
        return initDatabase(withData);
    }

    @PostMapping("/database")
    public Result<String> initDatabase(@RequestParam(required = false, defaultValue = "false") boolean withData) {
        try {
            initService.initDatabase(withData);
            if (withData) {
                return Result.success("数据库初始化成功！已创建表结构并插入测试数据");
            }
            return Result.success("数据库初始化成功！已创建表结构");
        } catch (Exception e) {
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
}
