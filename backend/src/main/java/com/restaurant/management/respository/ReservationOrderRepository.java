package com.restaurant.management.respository;

import com.restaurant.management.models.ReservationOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationOrderRepository extends JpaRepository<ReservationOrderEntity, Long> {
    boolean existsByReservation_Customer_IdAndFood_IdAndReservation_Status(Long userId, Long foodId, String status);

}
