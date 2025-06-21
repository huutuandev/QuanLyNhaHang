package com.restaurant.management.respository;

import com.restaurant.management.models.FoodCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<FoodCategoryEntity, Integer> {

}
