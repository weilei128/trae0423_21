package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BorrowRecord;
import com.library.entity.FineRecord;
import com.library.mapper.FineRecordMapper;
import com.library.service.BorrowRecordService;
import com.library.service.FineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineRecordServiceImpl extends ServiceImpl<FineRecordMapper, FineRecord> implements FineRecordService {

    public static final int STATUS_UNPAID = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_PARTIAL = 2;

    @Value("${library.fine.per-day-fine:0.5}")
    private double perDayFine;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Override
    public Page<FineRecord> pageRecords(Integer pageNum, Integer pageSize, Long readerId, Integer status) {
        Page<FineRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FineRecord> wrapper = new LambdaQueryWrapper<>();

        if (readerId != null) {
            wrapper.eq(FineRecord::getReaderId, readerId);
        }
        if (status != null) {
            wrapper.eq(FineRecord::getStatus, status);
        }
        wrapper.orderByDesc(FineRecord::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<FineRecord> getByReaderId(Long readerId) {
        LambdaQueryWrapper<FineRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FineRecord::getReaderId, readerId)
                .orderByDesc(FineRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<FineRecord> getUnpaidByReaderId(Long readerId) {
        LambdaQueryWrapper<FineRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FineRecord::getReaderId, readerId)
                .in(FineRecord::getStatus, STATUS_UNPAID, STATUS_PARTIAL)
                .orderByAsc(FineRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public BigDecimal getTotalUnpaidAmount(Long readerId) {
        List<FineRecord> unpaid = getUnpaidByReaderId(readerId);
        BigDecimal total = BigDecimal.ZERO;
        for (FineRecord r : unpaid) {
            BigDecimal remaining = r.getFineAmount().subtract(
                    r.getPaidAmount() != null ? r.getPaidAmount() : BigDecimal.ZERO);
            total = total.add(remaining);
        }
        return total;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createFine(Long borrowRecordId, int overdueDays, BigDecimal amount) {
        BorrowRecord record = borrowRecordService.getById(borrowRecordId);
        if (record == null) {
            return false;
        }

        FineRecord fineRecord = new FineRecord();
        fineRecord.setReaderId(record.getReaderId());
        fineRecord.setBorrowRecordId(borrowRecordId);
        fineRecord.setOverdueDays(overdueDays);
        fineRecord.setFineAmount(amount);
        fineRecord.setPaidAmount(BigDecimal.ZERO);
        fineRecord.setStatus(STATUS_UNPAID);
        fineRecord.setFineDate(LocalDate.now());

        return this.save(fineRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payFine(Long fineId, BigDecimal amount) {
        FineRecord fineRecord = this.getById(fineId);
        if (fineRecord == null) {
            return false;
        }

        if (fineRecord.getStatus() == STATUS_PAID) {
            return false;
        }

        BigDecimal paidAmount = fineRecord.getPaidAmount() != null ? fineRecord.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = paidAmount.add(amount);

        int comparison = newPaidAmount.compareTo(fineRecord.getFineAmount());
        if (comparison >= 0) {
            fineRecord.setPaidAmount(fineRecord.getFineAmount());
            fineRecord.setStatus(STATUS_PAID);
            fineRecord.setPaidDate(LocalDate.now());
        } else {
            fineRecord.setPaidAmount(newPaidAmount);
            fineRecord.setStatus(STATUS_PARTIAL);
        }

        return this.updateById(fineRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndCreateOverdueFines() {
        List<BorrowRecord> overdueRecords = borrowRecordService.getOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            LambdaQueryWrapper<FineRecord> existingWrapper = new LambdaQueryWrapper<>();
            existingWrapper.eq(FineRecord::getBorrowRecordId, record.getId())
                    .in(FineRecord::getStatus, STATUS_UNPAID, STATUS_PARTIAL);

            if (this.count(existingWrapper) > 0) {
                continue;
            }

            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
            if (overdueDays > 0) {
                BigDecimal fineAmount = BigDecimal.valueOf(overdueDays)
                        .multiply(BigDecimal.valueOf(perDayFine))
                        .setScale(2, RoundingMode.HALF_UP);

                FineRecord fineRecord = new FineRecord();
                fineRecord.setReaderId(record.getReaderId());
                fineRecord.setBorrowRecordId(record.getId());
                fineRecord.setOverdueDays(Math.toIntExact(overdueDays));
                fineRecord.setFineAmount(fineAmount);
                fineRecord.setPaidAmount(BigDecimal.ZERO);
                fineRecord.setStatus(STATUS_UNPAID);
                fineRecord.setFineDate(LocalDate.now());

                this.save(fineRecord);
            }
        }
    }
}
