package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookCollection;

import java.util.List;

public interface BookCollectionService extends IService<BookCollection> {
    Page<BookCollection> pageCollections(Integer pageNum, Integer pageSize, Long bookId, String barcode, Integer status);
    BookCollection getByBarcode(String barcode);
    boolean updateStatus(Long id, Integer status);
    List<BookCollection> getByBookId(Long bookId);
    int countAvailableByBookId(Long bookId);
}
