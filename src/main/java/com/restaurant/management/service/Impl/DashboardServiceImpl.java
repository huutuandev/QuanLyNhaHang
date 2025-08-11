package com.restaurant.management.service.Impl;


import com.restaurant.management.responses.MonthlyRevenueResponse;
import com.restaurant.management.responses.RevenueStatsResponse;
import com.restaurant.management.responses.StatusCountResponse;
import com.restaurant.management.responses.DashboardResponse;
import com.restaurant.management.respository.*;
import com.restaurant.management.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final FoodRepository foodRepo;
    private final CategoryRepository categoryRepo;
    private final ReservationRepository reservationRepo;
    private final BillRepository billRepo;
    private final OrderRepository orderRepo;

    @Override
    public DashboardResponse getDashboard() {
        long totalUsers = userRepo.count();
        long totalPosts = postRepo.count();
        long totalFoods = foodRepo.count();
        long totalCategories = categoryRepo.count();

        int currentYear = LocalDate.now().getYear();
        List<MonthlyRevenueResponse> monthlyRevenues = billRepo.sumMonthlyRevenue(currentYear);
        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalCategories(totalCategories)
                .totalFoods(totalFoods)
                .monthlyRevenue(monthlyRevenues)
                .build();
    }

    @Override
    public RevenueStatsResponse getRevenueStats(LocalDate start, LocalDate end) {
        Double revenue = billRepo.sumTotalAmountBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
        double totalRevenue = revenue != null ? revenue : 0.0;

        long totalOrders = start.isBefore(end)
                ? orderRepo.countByCreatedAtBetween(start.atStartOfDay(), end.atTime(23, 59, 59))
                : 0;
        return RevenueStatsResponse.builder()
                .startDate(start)
                .endDate(end)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .build();
    }

    @Override
    public List<StatusCountResponse> countByYear(int year) {
        return reservationRepo.countByYear(year);
    }

    @Override
    public List<StatusCountResponse> countByMonth(int year, int month) {
        return reservationRepo.countByMonth(year, month);
    }
}
