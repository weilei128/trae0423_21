package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BorrowRecord;

import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {
    Page<BorrowRecord> pageRecords(Integer pageNum, Integer pageSize, Long readerId, Long bookId, Integer status);
    List<BorrowRecord> getByReaderId(Long readerId);
    List<BorrowRecord> getOverdueRecords();
    int countActiveByReaderId(Long readerId);
    boolean borrowBook(Long readerId, Long collectionId);
    boolean returnBook(Long recordId);
    boolean renewBook(Long recordId);
}
