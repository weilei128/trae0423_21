package com.library.controller;

import com.library.common.Result;
import com.library.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = statsService.getOverviewStats();
        return Result.success(stats);
    }

    @GetMapping("/borrow-ranking")
    public Result<List<Map<String, Object>>> getBorrowRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = statsService.getBorrowRanking(limit);
        return Result.success(ranking);
    }

    @GetMapping("/popular-books")
    public Result<List<Map<String, Object>>> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> books = statsService.getPopularBooks(limit);
        return Result.success(books);
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStats(
            @RequestParam(defaultValue = "6") int months) {
        List<Map<String, Object>> stats = statsService.getMonthlyStats(months);
        return Result.success(stats);
    }

    @GetMapping("/borrow-count")
    public Result<Long> getBorrowCount(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long count = statsService.getBorrowCount(startDate, endDate);
        return Result.success(count);
    }

    @GetMapping("/return-count")
    public Result<Long> getReturnCount(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long count = statsService.getReturnCount(startDate, endDate);
        return Result.success(count);
    }

    @GetMapping("/visit-count")
    public Result<Long> getVisitCount(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long count = statsService.getVisitCount(startDate, endDate);
        return Result.success(count);
    }
}
