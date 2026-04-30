package com.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsService {
    List<Map<String, Object>> getBorrowRanking(int limit);
    List<Map<String, Object>> getPopularBooks(int limit);
    long getVisitCount(LocalDate startDate, LocalDate endDate);
    long getBorrowCount(LocalDate startDate, LocalDate endDate);
    long getReturnCount(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getOverviewStats();
    List<Map<String, Object>> getMonthlyStats(int months);
}
