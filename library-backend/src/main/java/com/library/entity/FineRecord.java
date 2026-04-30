package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fine_record")
public class FineRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long readerId;

    private Long borrowRecordId;

    private Integer overdueDays;

    private BigDecimal fineAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private LocalDate fineDate;

    private LocalDate paidDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
