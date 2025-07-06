package com.restaurant.management.controller;

import com.restaurant.management.DTO.ReviewDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/review")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateReview(@AuthenticationPrincipal UserDTO userDTO,
                                                  @RequestBody ReviewDTO reviewDTO){
        reviewService.createOrUpdate(userDTO, reviewDTO);
        return ResponseEntity.ok("Đánh giá đã được ghi nhận!");
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteReview(@AuthenticationPrincipal UserDTO userDTO,
                                           @PathVariable Long id){
            reviewService.deleteById(id,userDTO);
            return  ResponseEntity.ok("Xóa bình luận thành công");
    }
}
