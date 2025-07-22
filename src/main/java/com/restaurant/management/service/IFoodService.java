package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IFoodService {
    FoodDetailResponse getFoodsAndReviews(Long id);
    NewFoodResponse getNewFoods();
    Page<FoodDTO> getAllFoods(int page, int size);
    FoodDTO getById(Long id);
    FoodDTO createOrUpdate(FoodDTO foodDTO);
    void deleteById(Long id);
}
