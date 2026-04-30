package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Reader;

public interface ReaderService extends IService<Reader> {
    Page<Reader> pageReaders(Integer pageNum, Integer pageSize, String keyword, String type, Integer isBlacklist);
    Reader getByReaderCard(String readerCard);
    boolean addToBlacklist(Long id, String reason);
    boolean removeFromBlacklist(Long id);
    boolean canBorrow(Long readerId);
    int getCurrentBorrowCount(Long readerId);
}
