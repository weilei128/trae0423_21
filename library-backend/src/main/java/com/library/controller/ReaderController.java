package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Reader;
import com.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @GetMapping("/page")
    public Result<Page<Reader>> pageReaders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isBlacklist) {
        Page<Reader> page = readerService.pageReaders(pageNum, pageSize, keyword, type, isBlacklist);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Reader> getById(@PathVariable Long id) {
        Reader reader = readerService.getById(id);
        return reader != null ? Result.success(reader) : Result.error("读者不存在");
    }

    @GetMapping("/card/{readerCard}")
    public Result<Reader> getByReaderCard(@PathVariable String readerCard) {
        Reader reader = readerService.getByReaderCard(readerCard);
        return reader != null ? Result.success(reader) : Result.error("读者证不存在");
    }

    @PostMapping
    public Result<Reader> create(@RequestBody Reader reader) {
        if (reader.getIsBlacklist() == null) {
            reader.setIsBlacklist(0);
        }
        readerService.save(reader);
        return Result.success("添加成功", reader);
    }

    @PutMapping("/{id}")
    public Result<Reader> update(@PathVariable Long id, @RequestBody Reader reader) {
        reader.setId(id);
        readerService.updateById(reader);
        return Result.success("更新成功", reader);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        readerService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        readerService.removeByIds(ids);
        return Result.success();
    }

    @PutMapping("/{id}/blacklist/add")
    public Result<Void> addToBlacklist(@PathVariable Long id, @RequestParam(required = false) String reason) {
        boolean success = readerService.addToBlacklist(id, reason);
        return success ? Result.success() : Result.error("操作失败");
    }

    @PutMapping("/{id}/blacklist/remove")
    public Result<Void> removeFromBlacklist(@PathVariable Long id) {
        boolean success = readerService.removeFromBlacklist(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    @GetMapping("/{id}/canBorrow")
    public Result<Boolean> canBorrow(@PathVariable Long id) {
        boolean can = readerService.canBorrow(id);
        return Result.success(can);
    }

    @GetMapping("/{id}/borrowCount")
    public Result<Integer> getCurrentBorrowCount(@PathVariable Long id) {
        int count = readerService.getCurrentBorrowCount(id);
        return Result.success(count);
    }
}
