package com.restaurant.management.controller;

import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.MixFoodResponse;
import com.restaurant.management.service.IFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Validated
@Tag(name = "Web Public", description = "Public endpoints for general website landing pages")
public class WebController {

    private final IFoodService foodService;

    @GetMapping("/home")
    @Operation(summary = "Get home page data", description = "Retrieves public list of newly added and featured food items for the home screen.")
    public ResponseEntity<ApiResponse<MixFoodResponse>> getHomePageData() {
        log.info("Fetching public homepage food collections");
        MixFoodResponse newFoodResponse = foodService.getNewFoods();
        return ResponseEntity.ok(ApiResponse.success("Homepage data retrieved successfully", newFoodResponse));
    }
}
