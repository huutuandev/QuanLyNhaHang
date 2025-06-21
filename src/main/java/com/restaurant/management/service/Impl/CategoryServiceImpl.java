package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.FoodCategoryDTO;
import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.FoodCategoryEntity;
import com.restaurant.management.respository.CategoryRepository;
import com.restaurant.management.respository.FoodRepository;
import com.restaurant.management.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

//    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<FoodCategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(cat -> {
            List<FoodDTO> foods = cat.getFoods().stream().limit(4).map(food ->
                    FoodDTO.builder()
                            .id(food.getId())
                            .name(food.getName())
                            .description(food.getDescription())
                            .price(food.getPrice())
                            .imageUrl(food.getImageUrl())
                            .build()
            ).collect(Collectors.toList());

            return FoodCategoryDTO.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .foods(foods)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public FoodCategoryDTO findByIdWithFoods(Integer id) {
        FoodCategoryEntity cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found id = " + id));
        List<FoodDTO> foods = cat.getFoods().stream().map(foodEntity ->
                FoodDTO.builder()
                        .id(foodEntity.getId())
                        .name(foodEntity.getName())
                        .price(foodEntity.getPrice())
                        .description(foodEntity.getDescription())
                        .imageUrl(foodEntity.getImageUrl())
                        .build()
        ).collect(Collectors.toList());
        return FoodCategoryDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .foods(foods)
                .build();
    }


}
