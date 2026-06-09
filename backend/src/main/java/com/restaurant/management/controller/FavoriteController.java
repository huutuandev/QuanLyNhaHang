package com.restaurant.management.controller;

import com.restaurant.management.security.CustomUserDetails;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.FavoriteResponse;
import com.restaurant.management.service.IFoodOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing customer's favorite food items")
public class FavoriteController {

    private final IFoodOrderService foodOrderService;

    @PostMapping("/{foodId}")
    @Operation(summary = "Add food to favorites", description = "Adds a specific food item to the authenticated user's favorites list.")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long foodId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Adding favorite: Food ID: {} for User: {}", foodId, currentUser.getId());
        foodOrderService.addFavorite(currentUser.getId(), foodId);
        return ResponseEntity.ok(ApiResponse.success("Food added to favorites successfully", null));
    }

    @DeleteMapping("/{foodId}")
    @Operation(summary = "Remove food from favorites", description = "Removes a specific food item from the authenticated user's favorites list.")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long foodId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Removing favorite: Food ID: {} for User: {}", foodId, currentUser.getId());
        foodOrderService.removeFavorite(currentUser.getId(), foodId);
        return ResponseEntity.ok(ApiResponse.success("Food removed from favorites successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get favorite foods list", description = "Retrieves all favorite food items of the authenticated user.")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Fetching favorites for User: {}", currentUser.getId());
        List<FavoriteResponse> favorites = foodOrderService.getMyFavorites(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved successfully", favorites));
    }
}
