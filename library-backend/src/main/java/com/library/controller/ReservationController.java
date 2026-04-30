package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Reservation;
import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/page")
    public Result<Page<Reservation>> pageReservations(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status) {
        Page<Reservation> page = reservationService.pageReservations(pageNum, pageSize, readerId, bookId, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id) {
        Reservation reservation = reservationService.getById(id);
        return reservation != null ? Result.success(reservation) : Result.error("预约不存在");
    }

    @GetMapping("/reader/{readerId}")
    public Result<List<Reservation>> getByReaderId(@PathVariable Long readerId) {
        List<Reservation> list = reservationService.getByReaderId(readerId);
        return Result.success(list);
    }

    @GetMapping("/book/{bookId}")
    public Result<List<Reservation>> getByBookId(@PathVariable Long bookId) {
        List<Reservation> list = reservationService.getByBookId(bookId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> reserveBook(
            @RequestParam Long readerId,
            @RequestParam Long bookId) {
        boolean success = reservationService.reserveBook(readerId, bookId);
        return success ? Result.success() : Result.error("预约失败");
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelReservation(@PathVariable Long id) {
        boolean success = reservationService.cancelReservation(id);
        return success ? Result.success() : Result.error("取消预约失败");
    }

    @PutMapping("/{id}/fulfill")
    public Result<Void> fulfillReservation(@PathVariable Long id) {
        boolean success = reservationService.fulfillReservation(id);
        return success ? Result.success() : Result.error("履约失败");
    }

    @PostMapping("/check-expired")
    public Result<Void> checkExpiredReservations() {
        reservationService.checkExpiredReservations();
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        reservationService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        reservationService.removeByIds(ids);
        return Result.success();
    }
}
