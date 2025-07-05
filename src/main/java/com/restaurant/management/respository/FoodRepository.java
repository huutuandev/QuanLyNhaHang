package com.restaurant.management.respository;

import com.restaurant.management.models.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodEntity,Long> {
    List<FoodEntity> findByCategoryId(Integer categoryId);
}
