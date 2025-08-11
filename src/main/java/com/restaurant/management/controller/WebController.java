package com.restaurant.management.controller;

import com.restaurant.management.responses.MixFoodResponse;
import com.restaurant.management.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Validated
public class WebController {

    private final IFoodService foodService;

    @GetMapping("/home")
    public ResponseEntity<MixFoodResponse> getHomePageData() {
        MixFoodResponse newFoodResponse = foodService.getNewFoods();
        return ResponseEntity.ok(newFoodResponse);
    }

}
