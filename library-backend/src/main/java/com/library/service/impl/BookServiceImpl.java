package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public Page<Book> pageBooks(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getIsbn, keyword));
        }
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(Book::getCategory, category);
        }
        wrapper.orderByDesc(Book::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchImport(List<Book> books) {
        this.saveBatch(books);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvailableCopies(Long bookId, int delta) {
        Book book = this.getById(bookId);
        if (book == null) {
            return false;
        }
        int newAvailable = book.getAvailableCopies() + delta;
        if (newAvailable < 0) {
            return false;
        }
        book.setAvailableCopies(newAvailable);
        return this.updateById(book);
    }
}
