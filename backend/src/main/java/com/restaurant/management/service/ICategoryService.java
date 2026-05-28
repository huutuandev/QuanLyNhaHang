package com.restaurant.management.service;

import com.restaurant.management.DTO.FoodCategoryDTO;


import java.util.List;

public interface ICategoryService {
    List<FoodCategoryDTO> findAll();
    FoodCategoryDTO findByIdWithFoods(Long id, int page, int size);
    FoodCategoryDTO createOrUpdate(FoodCategoryDTO foodCategoryDTO);
    void deleteById(Long id);
}
