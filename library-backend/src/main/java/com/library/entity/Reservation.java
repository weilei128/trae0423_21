package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long readerId;

    private Long bookId;

    private LocalDate reservationDate;

    private LocalDate expiryDate;

    private Integer status;

    private Integer queuePosition;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
