package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.FineRecord;
import com.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCollectionService bookCollectionService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private FineRecordService fineRecordService;

    private <T> List<T> safeList(java.util.function.Supplier<List<T>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private long safeCount(java.util.function.Supplier<Long> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public List<Map<String, Object>> getBorrowRanking(int limit) {
        List<BorrowRecord> allRecords = safeList(() -> borrowRecordService.list());
        Map<Long, Long> readerBorrowCount = new HashMap<>();

        for (BorrowRecord record : allRecords) {
            readerBorrowCount.merge(record.getReaderId(), 1L, Long::sum);
        }

        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(readerBorrowCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("readerId", sorted.get(i).getKey());
            item.put("borrowCount", sorted.get(i).getValue());
            item.put("rank", i + 1);
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getPopularBooks(int limit) {
        List<BorrowRecord> allRecords = safeList(() -> borrowRecordService.list());
        Map<Long, Long> bookBorrowCount = new HashMap<>();

        for (BorrowRecord record : allRecords) {
            if (record.getBookId() != null) {
                bookBorrowCount.merge(record.getBookId(), 1L, Long::sum);
            }
        }

        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(bookBorrowCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            Long bookId = sorted.get(i).getKey();
            Book book = null;
            try {
                book = bookService.getById(bookId);
            } catch (Exception e) {
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("bookId", bookId);
            item.put("title", book != null ? book.getTitle() : "Unknown");
            item.put("author", book != null ? book.getAuthor() : "");
            item.put("borrowCount", sorted.get(i).getValue());
            item.put("rank", i + 1);
            result.add(item);
        }

        return result;
    }

    @Override
    public long getVisitCount(LocalDate startDate, LocalDate endDate) {
        return safeCount(() -> readerService.count());
    }

    @Override
    public long getBorrowCount(LocalDate startDate, LocalDate endDate) {
        try {
            LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
            if (startDate != null) {
                wrapper.ge(BorrowRecord::getBorrowDate, startDate);
            }
            if (endDate != null) {
                wrapper.le(BorrowRecord::getBorrowDate, endDate);
            }
            return borrowRecordService.count(wrapper);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public long getReturnCount(LocalDate startDate, LocalDate endDate) {
        try {
            LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BorrowRecord::getStatus, BorrowRecordServiceImpl.STATUS_RETURNED);
            if (startDate != null) {
                wrapper.ge(BorrowRecord::getReturnDate, startDate);
            }
            if (endDate != null) {
                wrapper.le(BorrowRecord::getReturnDate, endDate);
            }
            return borrowRecordService.count(wrapper);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalBooks", safeCount(() -> bookService.count()));
        stats.put("totalCollections", safeCount(() -> bookCollectionService.count()));
        stats.put("totalReaders", safeCount(() -> readerService.count()));

        try {
            LambdaQueryWrapper<BorrowRecord> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.in(BorrowRecord::getStatus,
                    BorrowRecordServiceImpl.STATUS_BORROWED,
                    BorrowRecordServiceImpl.STATUS_OVERDUE,
                    BorrowRecordServiceImpl.STATUS_RENEWED);
            stats.put("activeBorrows", borrowRecordService.count(activeWrapper));
        } catch (Exception e) {
            stats.put("activeBorrows", 0L);
        }

        try {
            stats.put("overdueCount", borrowRecordService.getOverdueRecords().size());
        } catch (Exception e) {
            stats.put("overdueCount", 0);
        }

        try {
            LambdaQueryWrapper<FineRecord> unpaidWrapper = new LambdaQueryWrapper<>();
            unpaidWrapper.in(FineRecord::getStatus,
                    FineRecordServiceImpl.STATUS_UNPAID,
                    FineRecordServiceImpl.STATUS_PARTIAL);
            stats.put("unpaidFinesCount", fineRecordService.count(unpaidWrapper));
        } catch (Exception e) {
            stats.put("unpaidFinesCount", 0L);
        }

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        stats.put("monthlyBorrows", getBorrowCount(firstDayOfMonth, today));
        stats.put("monthlyReturns", getReturnCount(firstDayOfMonth, today));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getMonthlyStats(int months) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(today.minusMonths(i));
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", yearMonth.toString());
            item.put("borrowCount", getBorrowCount(startDate, endDate));
            item.put("returnCount", getReturnCount(startDate, endDate));
            result.add(item);
        }

        return result;
    }
}
