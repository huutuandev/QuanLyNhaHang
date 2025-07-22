package com.restaurant.management.controller;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/foods")
@RequiredArgsConstructor
@Validated
public class FoodController {
    private final IFoodService foodService;

    @GetMapping
    public ResponseEntity<PagedResponse<FoodDTO>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<FoodDTO> foodDTOPage = foodService.getAllFoods(page, size);
        return ResponseEntity.ok(new PagedResponse<>(foodDTOPage, foodDTOPage.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getById(@PathVariable Long id){
        FoodDTO foodDTO = foodService.getById(id);
        return ResponseEntity.ok(foodDTO);
    }
    @PostMapping
    public ResponseEntity<?> createOrUpdate(@Valid @RequestBody FoodDTO foodDTO){
        foodService.createOrUpdate(foodDTO);
        return ResponseEntity.ok("Thành Công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        foodService.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
