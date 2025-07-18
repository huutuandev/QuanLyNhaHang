package com.restaurant.management.controller;

import com.restaurant.management.DTO.RevenueStatsDTO;
import com.restaurant.management.responses.DashboardResponse;
import com.restaurant.management.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(){
        DashboardResponse dashboardResponse = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboardResponse);
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatsDTO> getRevenue(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(dashboardService.getRevenueStats(start, end));
    }
}
