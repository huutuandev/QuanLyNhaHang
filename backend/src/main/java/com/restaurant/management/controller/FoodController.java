package com.restaurant.management.controller;

import com.restaurant.management.dto.FoodDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/foods")
@RequiredArgsConstructor
@Validated
@Tag(name = "Foods", description = "Endpoints for managing restaurant menu food items")
public class FoodController {
    private final IFoodService foodService;

    @GetMapping
    @Operation(summary = "Get all foods", description = "Retrieves a paginated list of all active food items.")
    public ResponseEntity<ApiResponse<PagedResponse<FoodDTO>>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        log.info("Fetching active foods: Page: {}, Size: {}", page, size);
        Page<FoodDTO> foodDTOPage = foodService.getAllFoods(page, size);
        PagedResponse<FoodDTO> pagedResponse = new PagedResponse<>(foodDTOPage, foodDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Foods retrieved successfully", pagedResponse));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Get all deleted foods", description = "Retrieves a paginated list of soft-deleted food items (Admin only).")
    public ResponseEntity<ApiResponse<PagedResponse<FoodDTO>>> getAllFoodDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        log.info("Fetching soft-deleted foods: Page: {}, Size: {}", page, size);
        Page<FoodDTO> foodDTOPage = foodService.getAllFoodDeleted(page, size);
        PagedResponse<FoodDTO> pagedResponse = new PagedResponse<>(foodDTOPage, foodDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Deleted foods retrieved successfully", pagedResponse));
    }

    @PutMapping("/recovery/{id}")
    @Operation(summary = "Recover a soft-deleted food", description = "Restores a soft-deleted food item back to the active menu (Admin only).")
    public ResponseEntity<ApiResponse<FoodDTO>> UpdateFoodRecovery(@PathVariable Long id){
        log.info("Recovering food item with ID: {}", id);
        FoodDTO foodDTO = foodService.updateFoodRecovery(id);
        return ResponseEntity.ok(ApiResponse.success("Food item recovered successfully", foodDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get food item by ID", description = "Retrieves the details of a specific food item by its ID.")
    public ResponseEntity<ApiResponse<FoodDTO>> getById(@PathVariable Long id){
        log.info("Fetching food item with ID: {}", id);
        FoodDTO foodDTO = foodService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Food item retrieved successfully", foodDTO));
    }

    @PostMapping
    @Operation(summary = "Create or update food item", description = "Saves a new food item or updates an existing one.")
    public ResponseEntity<ApiResponse<String>> createOrUpdate(@Valid @RequestBody FoodDTO foodDTO){
        log.info("Creating/updating food item: {}", foodDTO.getName());
        foodService.createOrUpdate(foodDTO);
        return ResponseEntity.ok(ApiResponse.success("Thành Công", "Thành Công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete food item", description = "Soft-deletes a food item from the menu by its ID.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        log.info("Soft-deleting food item with ID: {}", id);
        foodService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Food item deleted successfully", null));
    }
}
