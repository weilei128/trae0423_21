package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Reservation;

import java.util.List;

public interface ReservationService extends IService<Reservation> {
    Page<Reservation> pageReservations(Integer pageNum, Integer pageSize, Long readerId, Long bookId, Integer status);
    List<Reservation> getByReaderId(Long readerId);
    List<Reservation> getByBookId(Long bookId);
    boolean reserveBook(Long readerId, Long bookId);
    boolean cancelReservation(Long id);
    boolean fulfillReservation(Long id);
    void checkExpiredReservations();
}
