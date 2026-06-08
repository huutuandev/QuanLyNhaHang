package com.restaurant.management.controller;

import com.restaurant.management.dto.FoodCategoryDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.service.ICategoryService;
import com.restaurant.management.service.IFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories", description = "Endpoints for managing food categories")
public class CategoryController {
    private final ICategoryService categoryService;
    private final IFoodService foodService;

    @GetMapping
    @Operation(summary = "Get all food categories with foods", description = "Retrieves all categories along with their foods.")
    public ResponseEntity<ApiResponse<List<FoodCategoryDTO>>> getAllCategoriesWithFoods() {
        log.info("Fetching all categories with foods");
        List<FoodCategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves details of a food category and a paginated list of foods in it.")
    public ResponseEntity<ApiResponse<FoodCategoryDTO>> getCategoryById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        log.info("Fetching category ID: {}, Page: {}, Size: {}", id, page, size);
        FoodCategoryDTO foodCategoryDTO = categoryService.findByIdWithFoods(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", foodCategoryDTO));
    }

    @GetMapping("/foods/{id}")
    @Operation(summary = "Get food reviews detail", description = "Retrieves food info and all of its reviews.")
    public ResponseEntity<ApiResponse<FoodDetailResponse>> getFoodsAndReviews(@PathVariable Long id) {
        log.info("Fetching food and reviews for Food ID: {}", id);
        FoodDetailResponse foodDetailResponse = foodService.getFoodsAndReviews(id);
        return ResponseEntity.ok(ApiResponse.success("Food detail and reviews retrieved", foodDetailResponse));
    }

    @PostMapping
    @Operation(summary = "Create or update category", description = "Saves or updates a food category.")
    public ResponseEntity<ApiResponse<String>> createOrUpdate(@Valid @RequestBody FoodCategoryDTO foodCategoryDTO) {
        log.info("Creating/updating food category: {}", foodCategoryDTO.getName());
        categoryService.createOrUpdate(foodCategoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Category saved successfully", "Ok"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID", description = "Deletes a food category by its ID.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("Deleting category ID: {}", id);
        categoryService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
