package com.restaurant.management.respository;

import com.restaurant.management.models.FoodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<FoodEntity,Long> {
//    List<FoodEntity> findByCategoryId(Long categoryId);
    List<FoodEntity> findAllByIsDeletedFalse();
    List<FoodEntity> findAllByIsDeletedFalse(Sort createdAt);
    Optional<FoodEntity> findByIdAndIsDeletedFalse(Long id);
    Page<FoodEntity> findAllByIsDeletedFalse(Pageable pageable);
}
