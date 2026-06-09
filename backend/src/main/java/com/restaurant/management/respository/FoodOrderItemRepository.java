package com.restaurant.management.respository;

import com.restaurant.management.models.FoodOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodOrderItemRepository extends JpaRepository<FoodOrderItem, Long> {
}
