package com.restaurant.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatsDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private Long totalOrders;
}
