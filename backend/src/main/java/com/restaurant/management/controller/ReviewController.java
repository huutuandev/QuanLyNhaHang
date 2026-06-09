package com.restaurant.management.controller;

import com.restaurant.management.dto.ReviewDTO;
import com.restaurant.management.security.CustomUserDetails;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.service.IReviewService;
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
@RequestMapping("api/review")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reviews", description = "Endpoints for posting and deleting customer reviews on food items")
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create or update review", description = "Creates a new review or updates an existing review for a food item.")
    public ResponseEntity<ApiResponse<String>> createOrUpdateReview(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        log.info("Creating/updating review for Food ID: {} by User ID: {}", reviewDTO.getFoodId(), currentUser.getId());
        reviewService.createOrUpdate(currentUser.getId(), reviewDTO);
        return ResponseEntity.ok(ApiResponse.success("Đánh giá đã được ghi nhận!", "Đánh giá đã được ghi nhận!"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Deletes a customer review by its ID.")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        log.info("Deleting review ID: {} by User ID: {}", id, currentUser.getId());
        reviewService.deleteById(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Xóa bình luận thành công", "Xóa bình luận thành công"));
    }
}
