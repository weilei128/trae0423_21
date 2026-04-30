package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/page")
    public Result<Page<BorrowRecord>> pageRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status) {
        Page<BorrowRecord> page = borrowRecordService.pageRecords(pageNum, pageSize, readerId, bookId, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<BorrowRecord> getById(@PathVariable Long id) {
        BorrowRecord record = borrowRecordService.getById(id);
        return record != null ? Result.success(record) : Result.error("记录不存在");
    }

    @GetMapping("/reader/{readerId}")
    public Result<List<BorrowRecord>> getByReaderId(@PathVariable Long readerId) {
        List<BorrowRecord> list = borrowRecordService.getByReaderId(readerId);
        return Result.success(list);
    }

    @PostMapping("/borrow")
    public Result<Void> borrowBook(
            @RequestParam Long readerId,
            @RequestParam Long collectionId) {
        boolean success = borrowRecordService.borrowBook(readerId, collectionId);
        return success ? Result.success() : Result.error("借书失败");
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        boolean success = borrowRecordService.returnBook(id);
        return success ? Result.success() : Result.error("还书失败");
    }

    @PutMapping("/{id}/renew")
    public Result<Void> renewBook(@PathVariable Long id) {
        boolean success = borrowRecordService.renewBook(id);
        return success ? Result.success() : Result.error("续借失败");
    }

    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> getOverdueRecords() {
        List<BorrowRecord> list = borrowRecordService.getOverdueRecords();
        return Result.success(list);
    }

    @PostMapping
    public Result<BorrowRecord> create(@RequestBody BorrowRecord record) {
        borrowRecordService.save(record);
        return Result.success("添加成功", record);
    }

    @PutMapping("/{id}")
    public Result<BorrowRecord> update(@PathVariable Long id, @RequestBody BorrowRecord record) {
        record.setId(id);
        borrowRecordService.updateById(record);
        return Result.success("更新成功", record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        borrowRecordService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        borrowRecordService.removeByIds(ids);
        return Result.success();
    }
}
