package com.restaurant.management.controller;

import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.DashboardResponse;
import com.restaurant.management.responses.RevenueStatsResponse;
import com.restaurant.management.responses.StatusCountResponse;
import com.restaurant.management.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dashboard", description = "Endpoints for retrieving analytics and dashboard metrics (Admin only)")
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get overall dashboard stats", description = "Retrieves aggregated metrics (revenue, total bills, total reservations, active users, popular foods) for the dashboard.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        log.info("Fetching overall dashboard statistics");
        DashboardResponse dashboardResponse = dashboardService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", dashboardResponse));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue stats by date range", description = "Retrieves sales revenue stats aggregated within a specific date range.")
    public ResponseEntity<ApiResponse<RevenueStatsResponse>> getRevenue(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        log.info("Fetching revenue statistics from {} to {}", start, end);
        RevenueStatsResponse revenueStats = dashboardService.getRevenueStats(start, end);
        return ResponseEntity.ok(ApiResponse.success("Revenue statistics retrieved successfully", revenueStats));
    }

    @GetMapping("/status")
    @Operation(summary = "Get reservation status counts", description = "Retrieves counts of reservations by their status, grouped by year or a specific month of that year.")
    public ResponseEntity<ApiResponse<List<StatusCountResponse>>> getReservationStatusStats(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month
    ) {
        log.info("Fetching reservation status statistics for Year: {}, Month: {}", year, month);
        List<StatusCountResponse> stats;
        if (month == null) {
            stats = dashboardService.countByYear(year);
        } else {
            stats = dashboardService.countByMonth(year, month);
        }
        return ResponseEntity.ok(ApiResponse.success("Reservation status statistics retrieved successfully", stats));
    }
}
