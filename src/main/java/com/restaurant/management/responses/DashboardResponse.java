package com.restaurant.management.responses;

import com.restaurant.management.DTO.MonthlyRevenueDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalPosts;
    private long totalFoods;
    private long totalCategories;
    private List<MonthlyRevenueDTO> monthlyRevenue;

}
