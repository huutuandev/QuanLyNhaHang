package com.restaurant.management.respository;

import com.restaurant.management.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
