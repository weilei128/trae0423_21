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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记(0:未删除 1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_borrow_record_id` (`borrow_record_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='罚款记录表';

-- 插入测试数据
-- 图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `category`, `total_copies`, `available_copies`) VALUES
('9787111213826', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '计算机', 5, 5),
('9787302251088', '算法导论', 'Thomas H. Cormen', '清华大学出版社', '计算机', 3, 3),
('9787111407010', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '计算机', 4, 4),
('9787111128069', '设计模式：可复用面向对象软件的基础', 'Erich Gamma', '机械工业出版社', '计算机', 2, 2),
('9787115279460', 'JavaScript高级程序设计', 'Nicholas C. Zakas', '人民邮电出版社', '计算机', 5, 5),
('9787111503903', 'Spring实战', 'Craig Walls', '机械工业出版社', '计算机', 3, 3),
('9787121287756', 'Vue.js实战', '梁灏', '电子工业出版社', '计算机', 4, 4),
('9787111376637', 'MySQL必知必会', 'Ben Forta', '机械工业出版社', '计算机', 6, 6),
('9787111217077', '数据结构与算法分析', 'Mark Allen Weiss', '机械工业出版社', '计算机', 3, 3),
('9787111358596', 'Effective Java中文版', 'Joshua Bloch', '机械工业出版社', '计算机', 4, 4),
('9787544247246', '百年孤独', '加西亚·马尔克斯', '南海出版公司', '文学', 5, 5),
('9787020024759', '红楼梦', '曹雪芹', '人民文学出版社', '文学', 3, 3),
('9787544270878', '活着', '余华', '作家出版社', '文学', 5, 5),
('9787544244046', '飘', '玛格丽特·米切尔', '上海译文出版社', '文学', 3, 3),
('9787532736799', '追风筝的人', '卡勒德·胡赛尼', '上海人民出版社', '文学', 4, 4);

-- 馆藏
INSERT INTO `collection` (`book_id`, `barcode`, `location`, `status`) VALUES
(1, 'LIB-B001-001', 'A区-01架-01层', 0),
(1, 'LIB-B001-002', 'A区-01架-01层', 0),
(1, 'LIB-B001-003', 'A区-01架-01层', 0),
(1, 'LIB-B001-004', 'A区-01架-01层', 0),
(1, 'LIB-B001-005', 'A区-01架-01层', 0),
(2, 'LIB-B002-001', 'A区-01架-02层', 0),
(2, 'LIB-B002-002', 'A区-01架-02层', 0),
(2, 'LIB-B002-003', 'A区-01架-02层', 0),
(3, 'LIB-B003-001', 'A区-01架-03层', 0),
(3, 'LIB-B003-002', 'A区-01架-03层', 0),
(3, 'LIB-B003-003', 'A区-01架-03层', 0),
(3, 'LIB-B003-004', 'A区-01架-03层', 0),
(11, 'LIB-B011-001', 'B区-01架-01层', 0),
(11, 'LIB-B011-002', 'B区-01架-01层', 0),
(11, 'LIB-B011-003', 'B区-01架-01层', 0),
(13, 'LIB-B013-001', 'B区-01架-02层', 0),
(13, 'LIB-B013-002', 'B区-01架-02层', 0),
(13, 'LIB-B013-003', 'B区-01架-02层', 0);

-- 读者
INSERT INTO `reader` (`reader_card`, `name`, `gender`, `phone`, `email`, `type`, `max_borrow_count`, `is_blacklist`) VALUES
('RD2024001', '张三', 1, '13800138001', 'zhangsan@example.com', 'student', 10, 0),
('RD2024002', '李四', 0, '13800138002', 'lisi@example.com', 'student', 10, 0),
('RD2024003', '王五', 1, '13800138003', 'wangwu@example.com', 'teacher', 20, 0),
('RD2024004', '赵六', 2, '13800138004', 'zhaoliu@example.com', 'student', 10, 0),
('RD2024005', '钱七', 1, '13800138005', 'qianqi@example.com', 'student', 10, 0);
