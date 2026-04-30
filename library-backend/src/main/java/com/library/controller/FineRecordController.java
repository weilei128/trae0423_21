package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.FineRecord;
import com.library.service.FineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fine-records")
public class FineRecordController {

    @Autowired
    private FineRecordService fineRecordService;

    @GetMapping("/page")
    public Result<Page<FineRecord>> pageRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Integer status) {
        Page<FineRecord> page = fineRecordService.pageRecords(pageNum, pageSize, readerId, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<FineRecord> getById(@PathVariable Long id) {
        FineRecord record = fineRecordService.getById(id);
        return record != null ? Result.success(record) : Result.error("罚款记录不存在");
    }

    @GetMapping("/reader/{readerId}")
    public Result<List<FineRecord>> getByReaderId(@PathVariable Long readerId) {
        List<FineRecord> list = fineRecordService.getByReaderId(readerId);
        return Result.success(list);
    }

    @GetMapping("/reader/{readerId}/unpaid")
    public Result<List<FineRecord>> getUnpaidByReaderId(@PathVariable Long readerId) {
        List<FineRecord> list = fineRecordService.getUnpaidByReaderId(readerId);
        return Result.success(list);
    }

    @GetMapping("/reader/{readerId}/unpaid/total")
    public Result<BigDecimal> getTotalUnpaidAmount(@PathVariable Long readerId) {
        BigDecimal total = fineRecordService.getTotalUnpaidAmount(readerId);
        return Result.success(total);
    }

    @PostMapping
    public Result<FineRecord> create(@RequestBody FineRecord record) {
        fineRecordService.save(record);
        return Result.success("添加成功", record);
    }

    @PutMapping("/{id}")
    public Result<FineRecord> update(@PathVariable Long id, @RequestBody FineRecord record) {
        record.setId(id);
        fineRecordService.updateById(record);
        return Result.success("更新成功", record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fineRecordService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        fineRecordService.removeByIds(ids);
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    public Result<Void> payFine(@PathVariable Long id, @RequestParam BigDecimal amount) {
        boolean success = fineRecordService.payFine(id, amount);
        return success ? Result.success() : Result.error("缴费失败");
    }

    @PostMapping("/check-overdue")
    public Result<Void> checkAndCreateOverdueFines() {
        fineRecordService.checkAndCreateOverdueFines();
        return Result.success();
    }
}
