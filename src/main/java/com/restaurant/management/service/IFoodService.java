package com.restaurant.management.service;

import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;

public interface IFoodService {
    FoodDetailResponse getFoodsAndReviews(Integer id);
    NewFoodResponse getNewFoods();
}
