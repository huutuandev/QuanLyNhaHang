package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.MixFoodResponse;
import org.springframework.data.domain.Page;

public interface IFoodService {
    FoodDetailResponse getFoodsAndReviews(Long id);
    MixFoodResponse getNewFoods();
    Page<FoodDTO> getAllFoods(int page, int size);
    FoodDTO getById(Long id);
    FoodDTO createOrUpdate(FoodDTO foodDTO);
    void deleteById(Long id);
}
