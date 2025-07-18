package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;

import java.util.List;

public interface IFoodService {
    FoodDetailResponse getFoodsAndReviews(Long id);
    NewFoodResponse getNewFoods();
    List<FoodDTO> getAllFoods();
    FoodDTO getById(Long id);
    FoodDTO createOrUpdate(FoodDTO foodDTO);
    void deleteById(Long id);
}
