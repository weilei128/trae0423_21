package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Reservation;
import com.library.mapper.ReservationMapper;
import com.library.service.BookService;
import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FULFILLED = 1;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_EXPIRED = 3;

    @Autowired
    private BookService bookService;

    @Override
    public Page<Reservation> pageReservations(Integer pageNum, Integer pageSize, Long readerId, Long bookId, Integer status) {
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();

        if (readerId != null) {
            wrapper.eq(Reservation::getReaderId, readerId);
        }
        if (bookId != null) {
            wrapper.eq(Reservation::getBookId, bookId);
        }
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<Reservation> getByReaderId(Long readerId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getReaderId, readerId)
                .orderByDesc(Reservation::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Reservation> getByBookId(Long bookId) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getBookId, bookId)
                .eq(Reservation::getStatus, STATUS_PENDING)
                .orderByAsc(Reservation::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reserveBook(Long readerId, Long bookId) {
        LambdaQueryWrapper<Reservation> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(Reservation::getReaderId, readerId)
                .eq(Reservation::getBookId, bookId)
                .eq(Reservation::getStatus, STATUS_PENDING);
        if (this.count(existingWrapper) > 0) {
            return false;
        }

        List<Reservation> pendingReservations = getByBookId(bookId);
        int queuePosition = pendingReservations.size() + 1;

        Reservation reservation = new Reservation();
        reservation.setReaderId(readerId);
        reservation.setBookId(bookId);
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpiryDate(LocalDate.now().plusDays(7));
        reservation.setStatus(STATUS_PENDING);
        reservation.setQueuePosition(queuePosition);

        return this.save(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReservation(Long id) {
        Reservation reservation = this.getById(id);
        if (reservation == null || reservation.getStatus() != STATUS_PENDING) {
            return false;
        }

        reservation.setStatus(STATUS_CANCELLED);
        this.updateById(reservation);

        updateQueuePositions(reservation.getBookId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean fulfillReservation(Long id) {
        Reservation reservation = this.getById(id);
        if (reservation == null || reservation.getStatus() != STATUS_PENDING) {
            return false;
        }

        reservation.setStatus(STATUS_FULFILLED);
        this.updateById(reservation);

        updateQueuePositions(reservation.getBookId());

        return true;
    }

    private void updateQueuePositions(Long bookId) {
        List<Reservation> pendingReservations = getByBookId(bookId);
        for (int i = 0; i < pendingReservations.size(); i++) {
            Reservation r = pendingReservations.get(i);
            r.setQueuePosition(i + 1);
            this.updateById(r);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkExpiredReservations() {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, STATUS_PENDING)
                .lt(Reservation::getExpiryDate, LocalDate.now());

        List<Reservation> expiredReservations = this.list(wrapper);
        for (Reservation r : expiredReservations) {
            r.setStatus(STATUS_EXPIRED);
            this.updateById(r);
        }
    }
}
