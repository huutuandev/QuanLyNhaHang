package com.restaurant.management.respository;

import com.restaurant.management.models.FoodOrder;
import com.restaurant.management.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByUserOrderByCreatedAtDesc(UserEntity user);
    Optional<FoodOrder> findByOrderCode(String orderCode);

    @Query("SELECT o FROM FoodOrder o WHERE " +
            "(:status IS NULL OR o.orderStatus = :status) AND " +
            "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
            "(:query IS NULL OR o.orderCode LIKE %:query% OR o.receiverName LIKE %:query% OR o.receiverPhone LIKE %:query%)")
    Page<FoodOrder> searchOrders(
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("query") String query,
            Pageable pageable
    );
}
