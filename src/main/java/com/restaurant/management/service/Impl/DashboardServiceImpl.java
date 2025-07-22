package com.restaurant.management.service.Impl;


import com.restaurant.management.requests.MonthlyRevenueRequest;
import com.restaurant.management.requests.RevenueStatsRequest;
import com.restaurant.management.requests.StatusCountRequest;
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
        List<MonthlyRevenueRequest> monthlyRevenues = billRepo.sumMonthlyRevenue(currentYear);
        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalCategories(totalCategories)
                .totalFoods(totalFoods)
                .monthlyRevenue(monthlyRevenues)
                .build();
    }

    @Override
    public RevenueStatsRequest getRevenueStats(LocalDate start, LocalDate end) {
        Double totalRevenue = billRepo.sumTotalAmountBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
        Long totalOrders = orderRepo.countByCreatedAtBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
        return RevenueStatsRequest.builder()
                .startDate(start)
                .endDate(end)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .build();
    }

    @Override
    public List<StatusCountRequest> countByYear(int year) {
        return reservationRepo.countByYear(year);
    }

    @Override
    public List<StatusCountRequest> countByMonth(int year, int month) {
        return reservationRepo.countByMonth(year, month);
    }
}
