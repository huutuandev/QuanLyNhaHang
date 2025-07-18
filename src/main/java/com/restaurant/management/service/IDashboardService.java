package com.restaurant.management.service;

import com.restaurant.management.DTO.RevenueStatsDTO;
import com.restaurant.management.responses.DashboardResponse;

import java.time.LocalDate;

public interface IDashboardService {
    DashboardResponse getDashboard();
    RevenueStatsDTO getRevenueStats(LocalDate start, LocalDate end);
}
