package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Book;

import java.util.List;

public interface BookService extends IService<Book> {
    Page<Book> pageBooks(Integer pageNum, Integer pageSize, String keyword, String category);
    void batchImport(List<Book> books);
    boolean updateAvailableCopies(Long bookId, int delta);
}
