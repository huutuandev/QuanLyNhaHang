package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodCategoryDTO;


import java.util.List;

public interface ICategoryService {
    List<FoodCategoryDTO> findAll();
    FoodCategoryDTO findByIdWithFoods(Integer id, int page, int size);
}
