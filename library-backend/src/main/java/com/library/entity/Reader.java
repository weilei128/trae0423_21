package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reader")
public class Reader {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String readerCard;

    private String name;

    private String gender;

    private String phone;

    private String email;

    private String type;

    private Integer maxBorrowCount;

    private LocalDate expiryDate;

    private Integer isBlacklist;

    private String blacklistReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
