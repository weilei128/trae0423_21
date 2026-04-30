package com.library.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = "系统内部错误，请联系管理员";
        }
        if (message.contains("doesn't exist") || message.contains("不存在")) {
            message = "数据库表不存在，请先初始化数据库";
        } else if (message.contains("Connection") || message.contains("connect")) {
            message = "数据库连接失败";
        }
        return Result.error(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数异常", e);
        return Result.error(e.getMessage() != null ? e.getMessage() : "参数错误");
    }
}
