package com.restaurant.management.respository;

import com.restaurant.management.models.FoodCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<FoodCategoryEntity, Long> {
    List<FoodCategoryEntity> findAllByIsDeletedFalse();
    Optional<FoodCategoryEntity> findByIdAndIsDeletedFalse(Long id);

}
