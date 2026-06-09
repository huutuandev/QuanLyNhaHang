package com.restaurant.management.respository;

import com.restaurant.management.models.CartItem;
import com.restaurant.management.models.FoodEntity;
import com.restaurant.management.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(UserEntity user);
    Optional<CartItem> findByUserAndFood(UserEntity user, FoodEntity food);
    void deleteByUser(UserEntity user);
}
