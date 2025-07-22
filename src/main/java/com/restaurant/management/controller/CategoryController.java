package com.restaurant.management.controller;


import com.restaurant.management.DTO.FoodCategoryDTO;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.service.ICategoryService;
import com.restaurant.management.service.IFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final ICategoryService categoryService;
    private final IFoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodCategoryDTO>> getAllCategoriesWithFoods()
    {
        List<FoodCategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size)
    {
        FoodCategoryDTO foodCategoryDTO = categoryService.findByIdWithFoods(id,page,size);
        return ResponseEntity.ok(foodCategoryDTO);
    }
    @GetMapping("/foods/{id}")
    public ResponseEntity<FoodDetailResponse> getFoodsAndReviews(@PathVariable Long id)
    {
        FoodDetailResponse foodDetailResponse = foodService.getFoodsAndReviews(id);
        return ResponseEntity.ok(foodDetailResponse);
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdate(@Valid @RequestBody FoodCategoryDTO foodCategoryDTO){
            categoryService.createOrUpdate(foodCategoryDTO);
            return ResponseEntity.ok("Ok");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
