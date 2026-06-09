package com.restaurant.management.respository;

import com.restaurant.management.models.Favorite;
import com.restaurant.management.models.FoodEntity;
import com.restaurant.management.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(UserEntity user);
    Optional<Favorite> findByUserAndFood(UserEntity user, FoodEntity food);
    boolean existsByUserAndFood(UserEntity user, FoodEntity food);
    void deleteByUserAndFood(UserEntity user, FoodEntity food);
}
