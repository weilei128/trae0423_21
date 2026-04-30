-- 校园图书馆管理系统数据库初始化脚本
-- 数据库: db1

-- 图书表
CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN号',
    `title` VARCHAR(200) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `publish_date` VARCHAR(20) DEFAULT NULL COMMENT '出版日期',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `description` TEXT DEFAULT NULL COMMENT '简介',
    `total_copies` INT DEFAULT 1 COMMENT '总册数',
    `available_copies` INT DEFAULT 1 COMMENT '可借册数',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_isbn` (`isbn`),
    KEY `idx_title` (`title`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 馆藏表
CREATE TABLE IF NOT EXISTS `collection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `barcode` VARCHAR(50) NOT NULL COMMENT '馆藏条码',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '库位',
    `status` INT DEFAULT 0 COMMENT '状态(0:在架 1:已借出 2:已预约 3:损坏)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_barcode` (`barcode`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='馆藏表';

-- 读者表
CREATE TABLE IF NOT EXISTS `reader` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reader_card` VARCHAR(50) NOT NULL COMMENT '读者证号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` INT DEFAULT 0 COMMENT '性别(0:未知 1:男 2:女)',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `type` VARCHAR(20) DEFAULT 'student' COMMENT '读者类型(student:学生 teacher:教师 other:其他)',
    `max_borrow_count` INT DEFAULT 10 COMMENT '最大借阅册数',
    `is_blacklist` INT DEFAULT 0 COMMENT '黑名单标记(0:正常 1:黑名单)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reader_card` (`reader_card`),
    KEY `idx_name` (`name`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS `borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reader_id` BIGINT NOT NULL COMMENT '读者ID',
    `collection_id` BIGINT NOT NULL COMMENT '馆藏ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `borrow_date` DATE NOT NULL COMMENT '借书日期',
    `due_date` DATE NOT NULL COMMENT '应还日期',
    `return_date` DATE DEFAULT NULL COMMENT '还书日期',
    `status` INT DEFAULT 0 COMMENT '状态(0:借阅中 1:已归还 2:已超期 3:已续借)',
    `renew_count` INT DEFAULT 0 COMMENT '续借次数',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`),
    KEY `idx_borrow_date` (`borrow_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS `reservation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reader_id` BIGINT NOT NULL COMMENT '读者ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `reservation_date` DATE NOT NULL COMMENT '预约日期',
    `expire_date` DATE DEFAULT NULL COMMENT '预约过期日期',
    `queue_position` INT DEFAULT 1 COMMENT '队列位置',
    `status` INT DEFAULT 0 COMMENT '状态(0:预约中 1:已履约 2:已取消 3:已过期)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- 罚款记录表
CREATE TABLE IF NOT EXISTS `fine_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reader_id` BIGINT NOT NULL COMMENT '读者ID',
    `borrow_record_id` BIGINT NOT NULL COMMENT '借阅记录ID',
    `overdue_days` INT DEFAULT 0 COMMENT '超期天数',
    `fine_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '罚款金额',
    `paid_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '已缴金额',
    `fine_date` DATE NOT NULL COMMENT '罚款日期',
    `paid_date` DATE DEFAULT NULL COMMENT '缴费日期',
    `status` INT DEFAULT 0 COMMENT '状态(0:未缴 1:已缴 2:部分缴)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_borrow_record_id` (`borrow_record_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='罚款记录表';
