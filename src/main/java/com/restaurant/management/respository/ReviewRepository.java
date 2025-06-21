package com.restaurant.management.respository;


import com.restaurant.management.models.FoodReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<FoodReviewEntity,Integer> {
    List<FoodReviewEntity> findAllByOrderByRatingDesc();
}
