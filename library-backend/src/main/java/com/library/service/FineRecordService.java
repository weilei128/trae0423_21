package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.FineRecord;

import java.math.BigDecimal;
import java.util.List;

public interface FineRecordService extends IService<FineRecord> {
    Page<FineRecord> pageRecords(Integer pageNum, Integer pageSize, Long readerId, Integer status);
    List<FineRecord> getByReaderId(Long readerId);
    List<FineRecord> getUnpaidByReaderId(Long readerId);
    BigDecimal getTotalUnpaidAmount(Long readerId);
    boolean createFine(Long borrowRecordId, int overdueDays, BigDecimal amount);
    boolean payFine(Long fineId, BigDecimal amount);
    void checkAndCreateOverdueFines();
}
