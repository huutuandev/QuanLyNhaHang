package com.restaurant.management.service;

import com.restaurant.management.requests.RevenueStatsRequest;
import com.restaurant.management.requests.StatusCountRequest;
import com.restaurant.management.responses.DashboardResponse;

import java.time.LocalDate;
import java.util.List;

public interface IDashboardService {
    DashboardResponse getDashboard();
    RevenueStatsRequest getRevenueStats(LocalDate start, LocalDate end);
    List<StatusCountRequest> countByYear(int year);
    List<StatusCountRequest> countByMonth(int year, int month);

}
