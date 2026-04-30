package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Reader;
import com.library.mapper.ReaderMapper;
import com.library.service.BorrowRecordService;
import com.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements ReaderService {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Override
    public Page<Reader> pageReaders(Integer pageNum, Integer pageSize, String keyword, String type, Integer isBlacklist) {
        Page<Reader> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Reader::getName, keyword)
                    .or().like(Reader::getReaderCard, keyword)
                    .or().like(Reader::getPhone, keyword));
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Reader::getType, type);
        }
        if (isBlacklist != null) {
            wrapper.eq(Reader::getIsBlacklist, isBlacklist);
        }
        wrapper.orderByDesc(Reader::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public Reader getByReaderCard(String readerCard) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reader::getReaderCard, readerCard);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToBlacklist(Long id, String reason) {
        Reader reader = this.getById(id);
        if (reader == null) {
            return false;
        }
        reader.setIsBlacklist(1);
        reader.setBlacklistReason(reason);
        return this.updateById(reader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromBlacklist(Long id) {
        Reader reader = this.getById(id);
        if (reader == null) {
            return false;
        }
        reader.setIsBlacklist(0);
        reader.setBlacklistReason(null);
        return this.updateById(reader);
    }

    @Override
    public boolean canBorrow(Long readerId) {
        Reader reader = this.getById(readerId);
        if (reader == null) {
            return false;
        }
        if (reader.getIsBlacklist() != null && reader.getIsBlacklist() == 1) {
            return false;
        }
        if (reader.getExpiryDate() != null && reader.getExpiryDate().isBefore(LocalDate.now())) {
            return false;
        }
        int currentCount = borrowRecordService.countActiveByReaderId(readerId);
        int maxCount = reader.getMaxBorrowCount() != null ? reader.getMaxBorrowCount() : 10;
        return currentCount < maxCount;
    }

    @Override
    public int getCurrentBorrowCount(Long readerId) {
        return borrowRecordService.countActiveByReaderId(readerId);
    }
}
