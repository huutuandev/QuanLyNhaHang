package com.restaurant.management.service;

import com.restaurant.management.responses.RevenueStatsResponse;
import com.restaurant.management.responses.StatusCountResponse;
import com.restaurant.management.responses.DashboardResponse;

import java.time.LocalDate;
import java.util.List;

public interface IDashboardService {
    DashboardResponse getDashboard();
    RevenueStatsResponse getRevenueStats(LocalDate start, LocalDate end);
    List<StatusCountResponse> countByYear(int year);
    List<StatusCountResponse> countByMonth(int year, int month);

}
