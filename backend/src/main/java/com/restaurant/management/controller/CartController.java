package com.restaurant.management.controller;

import com.restaurant.management.security.CustomUserDetails;
import com.restaurant.management.requests.AddToCartRequest;
import com.restaurant.management.requests.UpdateCartItemRequest;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.CartItemResponse;
import com.restaurant.management.responses.CartResponse;
import com.restaurant.management.service.IFoodOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
@Validated
@Tag(name = "Cart", description = "Endpoints for managing customer's shopping cart")
public class CartController {

    private final IFoodOrderService foodOrderService;

    @PostMapping("/items")
    @Operation(summary = "Add food to cart", description = "Adds a food item with a specified quantity to the authenticated user's cart.")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Adding food ID: {} to cart for User: {}", request.getFoodId(), currentUser.getId());
        CartItemResponse response = foodOrderService.addToCart(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Food added to cart successfully", response));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a specific cart item.")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItemQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Updating cart item ID: {} to quantity: {} for User: {}", id, request.getQuantity(), currentUser.getId());
        CartItemResponse response = foodOrderService.updateCartItemQuantity(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", response));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific cart item from the authenticated user's cart.")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Removing cart item ID: {} for User: {}", id, currentUser.getId());
        foodOrderService.removeCartItem(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get my cart", description = "Retrieves the authenticated user's shopping cart including subtotal and item count.")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Fetching cart for User: {}", currentUser.getId());
        CartResponse response = foodOrderService.getMyCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", response));
    }

    @DeleteMapping
    @Operation(summary = "Clear shopping cart", description = "Removes all items from the authenticated user's shopping cart.")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Clearing cart for User: {}", currentUser.getId());
        foodOrderService.clearCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }
}
