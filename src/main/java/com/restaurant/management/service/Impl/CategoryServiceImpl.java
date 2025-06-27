package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.FoodCategoryDTO;
import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.FoodCategoryEntity;
import com.restaurant.management.responses.PagedResponse;
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
                    .foods(PagedResponse.<FoodDTO>builder()
                            .content(foods)
                            .totalElements(foods.size())
                            .totalPages(1)
                            .size(4)
                            .number(0)
                            .build())
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public FoodCategoryDTO findByIdWithFoods(Integer id, int page, int size) {
        FoodCategoryEntity cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found id = " + id));

        List<FoodDTO> allFoods = cat.getFoods().stream().map(f ->
                FoodDTO.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .price(f.getPrice())
                        .description(f.getDescription())
                        .imageUrl(f.getImageUrl())
                        .build()
        ).collect(Collectors.toList());

        int totalElements = allFoods.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int from = page * size;
        int to = Math.min(from + size, totalElements);

        List<FoodDTO> paged = from >= totalElements ? Collections.emptyList() : allFoods.subList(from, to);

        PagedResponse<FoodDTO> foodPage = PagedResponse.<FoodDTO>builder()
                .content(paged)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .size(size)
                .number(page)
                .build();

        return FoodCategoryDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .foods(foodPage)
                .build();
    }




}
