package com.restaurant.management.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatsResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalRevenue;
    private long totalOrders;
}
