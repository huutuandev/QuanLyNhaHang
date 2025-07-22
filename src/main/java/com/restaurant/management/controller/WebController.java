package com.restaurant.management.controller;

import com.restaurant.management.DTO.*;
import com.restaurant.management.responses.NewFoodResponse;
import com.restaurant.management.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Validated
public class WebController {

    private final IFoodService foodService;

    @GetMapping("/home")
    public ResponseEntity<NewFoodResponse> getHomePageData() {
        NewFoodResponse newFoodResponse = foodService.getNewFoods();
        return ResponseEntity.ok(newFoodResponse);
    }

}
