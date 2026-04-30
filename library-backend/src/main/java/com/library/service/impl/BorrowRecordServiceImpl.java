package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookCollection;
import com.library.entity.BorrowRecord;
import com.library.entity.Reader;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import com.library.service.BookCollectionService;
import com.library.service.BorrowRecordService;
import com.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    public static final int STATUS_BORROWED = 0;
    public static final int STATUS_RETURNED = 1;
    public static final int STATUS_OVERDUE = 2;
    public static final int STATUS_RENEWED = 3;

    @Value("${library.borrow.default-days:30}")
    private int defaultBorrowDays;

    @Value("${library.borrow.renew-days:30}")
    private int renewDays;

    @Autowired
    private BookCollectionService bookCollectionService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @Override
    public Page<BorrowRecord> pageRecords(Integer pageNum, Integer pageSize, Long readerId, Long bookId, Integer status) {
        Page<BorrowRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();

        if (readerId != null) {
            wrapper.eq(BorrowRecord::getReaderId, readerId);
        }
        if (bookId != null) {
            wrapper.eq(BorrowRecord::getBookId, bookId);
        }
        if (status != null) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }
        wrapper.orderByDesc(BorrowRecord::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<BorrowRecord> getByReaderId(Long readerId) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getReaderId, readerId)
                .orderByDesc(BorrowRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getStatus, STATUS_BORROWED)
                .lt(BorrowRecord::getDueDate, LocalDate.now());
        return this.list(wrapper);
    }

    @Override
    public int countActiveByReaderId(Long readerId) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getReaderId, readerId)
                .in(BorrowRecord::getStatus, STATUS_BORROWED, STATUS_OVERDUE, STATUS_RENEWED);
        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean borrowBook(Long readerId, Long collectionId) {
        BookCollection collection = bookCollectionService.getById(collectionId);
        if (collection == null || collection.getStatus() != BookCollectionServiceImpl.STATUS_AVAILABLE) {
            return false;
        }

        if (!readerService.canBorrow(readerId)) {
            return false;
        }

        BorrowRecord record = new BorrowRecord();
        record.setReaderId(readerId);
        record.setCollectionId(collectionId);
        record.setBookId(collection.getBookId());
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(defaultBorrowDays));
        record.setStatus(STATUS_BORROWED);
        record.setRenewCount(0);

        this.save(record);

        bookCollectionService.updateStatus(collectionId, BookCollectionServiceImpl.STATUS_BORROWED);
        bookService.updateAvailableCopies(collection.getBookId(), -1);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBook(Long recordId) {
        BorrowRecord record = this.getById(recordId);
        if (record == null || record.getStatus() == STATUS_RETURNED) {
            return false;
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus(STATUS_RETURNED);
        this.updateById(record);

        bookCollectionService.updateStatus(record.getCollectionId(), BookCollectionServiceImpl.STATUS_AVAILABLE);
        bookService.updateAvailableCopies(record.getBookId(), 1);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean renewBook(Long recordId) {
        BorrowRecord record = this.getById(recordId);
        if (record == null) {
            return false;
        }

        if (record.getStatus() != STATUS_BORROWED && record.getStatus() != STATUS_RENEWED) {
            return false;
        }

        LocalDate newDueDate = record.getDueDate().plusDays(renewDays);
        record.setDueDate(newDueDate);
        record.setStatus(STATUS_RENEWED);
        record.setRenewCount(record.getRenewCount() != null ? record.getRenewCount() + 1 : 1);

        return this.updateById(record);
    }
}
