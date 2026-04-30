package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/page")
    public Result<Page<Book>> pageBooks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        Page<Book> page = bookService.pageBooks(pageNum, pageSize, keyword, category);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return book != null ? Result.success(book) : Result.error("图书不存在");
    }

    @PostMapping
    public Result<Book> create(@RequestBody Book book) {
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies() != null ? book.getTotalCopies() : 0);
        }
        bookService.save(book);
        return Result.success("添加成功", book);
    }

    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateById(book);
        return Result.success("更新成功", book);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        bookService.removeByIds(ids);
        return Result.success();
    }

    @PostMapping("/import")
    public Result<Void> batchImport(@RequestBody List<Book> books) {
        bookService.batchImport(books);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Book>> list() {
        List<Book> list = bookService.list();
        return Result.success(list);
    }
}
