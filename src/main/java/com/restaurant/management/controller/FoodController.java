package com.restaurant.management.controller;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.service.IFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/foods")
@RequiredArgsConstructor
@Validated
public class FoodController {
    private final IFoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodDTO>> getAllFoods(){
        List<FoodDTO> foodDTOS = foodService.getAllFoods();
        return ResponseEntity.ok(foodDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getById(@PathVariable Long id){
        FoodDTO foodDTO = foodService.getById(id);
        return ResponseEntity.ok(foodDTO);
    }
    @PostMapping
    public ResponseEntity<?> createOrUpdate(@RequestBody FoodDTO foodDTO){
        foodService.createOrUpdate(foodDTO);
        return ResponseEntity.ok("Thành Công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        foodService.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
