package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodCategoryDTO;
import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.models.FoodCategoryEntity;

import java.util.List;

public interface ICategoryService {
    List<FoodCategoryDTO> findAll();
    FoodCategoryDTO findByIdWithFoods(Integer id);

}
