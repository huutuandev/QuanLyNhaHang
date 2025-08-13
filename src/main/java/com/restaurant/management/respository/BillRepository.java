package com.restaurant.management.respository;

import com.restaurant.management.responses.MonthlyRevenueResponse;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.OrderEntity;
import com.restaurant.management.models.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<BillEntity, Long> {
    List<BillEntity> findByCashierId(Long cashierId);
    boolean existsByOrder(OrderEntity order);
    boolean existsByReservation(ReservationEntity reservation);
    BillEntity findByReservationId(Long id);

    @Query("SELECT new com.restaurant.management.responses.MonthlyRevenueResponse(MONTH(b.paidAt), SUM(b.paidAmount)) " +
            "FROM BillEntity b WHERE YEAR(b.paidAt) = :year GROUP BY MONTH(b.paidAt)")
    List<MonthlyRevenueResponse> sumMonthlyRevenue(@Param("year") int year);

    @Query("SELECT SUM(b.totalAmount) FROM BillEntity b WHERE b.paidAt BETWEEN :start AND :end")
    Double sumTotalAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
