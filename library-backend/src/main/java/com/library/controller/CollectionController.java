package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.BookCollection;
import com.library.service.BookCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private BookCollectionService bookCollectionService;

    @GetMapping("/page")
    public Result<Page<BookCollection>> pageCollections(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Integer status) {
        Page<BookCollection> page = bookCollectionService.pageCollections(pageNum, pageSize, bookId, barcode, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<BookCollection> getById(@PathVariable Long id) {
        BookCollection collection = bookCollectionService.getById(id);
        return collection != null ? Result.success(collection) : Result.error("馆藏不存在");
    }

    @GetMapping("/barcode/{barcode}")
    public Result<BookCollection> getByBarcode(@PathVariable String barcode) {
        BookCollection collection = bookCollectionService.getByBarcode(barcode);
        return collection != null ? Result.success(collection) : Result.error("条码不存在");
    }

    @PostMapping
    public Result<BookCollection> create(@RequestBody BookCollection collection) {
        if (collection.getStatus() == null) {
            collection.setStatus(0);
        }
        bookCollectionService.save(collection);
        return Result.success("添加成功", collection);
    }

    @PutMapping("/{id}")
    public Result<BookCollection> update(@PathVariable Long id, @RequestBody BookCollection collection) {
        collection.setId(id);
        bookCollectionService.updateById(collection);
        return Result.success("更新成功", collection);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookCollectionService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        bookCollectionService.removeByIds(ids);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = bookCollectionService.updateStatus(id, status);
        return success ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/book/{bookId}")
    public Result<List<BookCollection>> getByBookId(@PathVariable Long bookId) {
        List<BookCollection> list = bookCollectionService.getByBookId(bookId);
        return Result.success(list);
    }

    @GetMapping("/available/count/{bookId}")
    public Result<Integer> countAvailableByBookId(@PathVariable Long bookId) {
        int count = bookCollectionService.countAvailableByBookId(bookId);
        return Result.success(count);
    }
}
