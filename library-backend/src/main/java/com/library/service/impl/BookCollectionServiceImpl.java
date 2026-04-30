package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookCollection;
import com.library.mapper.BookCollectionMapper;
import com.library.service.BookCollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookCollectionServiceImpl extends ServiceImpl<BookCollectionMapper, BookCollection> implements BookCollectionService {

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_BORROWED = 1;
    public static final int STATUS_RESERVED = 2;
    public static final int STATUS_DAMAGED = 3;

    @Override
    public Page<BookCollection> pageCollections(Integer pageNum, Integer pageSize, Long bookId, String barcode, Integer status) {
        Page<BookCollection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookCollection> wrapper = new LambdaQueryWrapper<>();

        if (bookId != null) {
            wrapper.eq(BookCollection::getBookId, bookId);
        }
        if (StrUtil.isNotBlank(barcode)) {
            wrapper.like(BookCollection::getBarcode, barcode);
        }
        if (status != null) {
            wrapper.eq(BookCollection::getStatus, status);
        }
        wrapper.orderByDesc(BookCollection::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public BookCollection getByBarcode(String barcode) {
        LambdaQueryWrapper<BookCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCollection::getBarcode, barcode);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        BookCollection collection = this.getById(id);
        if (collection == null) {
            return false;
        }
        collection.setStatus(status);
        return this.updateById(collection);
    }

    @Override
    public List<BookCollection> getByBookId(Long bookId) {
        LambdaQueryWrapper<BookCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCollection::getBookId, bookId);
        return this.list(wrapper);
    }

    @Override
    public int countAvailableByBookId(Long bookId) {
        LambdaQueryWrapper<BookCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCollection::getBookId, bookId)
                .eq(BookCollection::getStatus, STATUS_AVAILABLE);
        return Math.toIntExact(this.count(wrapper));
    }
}
