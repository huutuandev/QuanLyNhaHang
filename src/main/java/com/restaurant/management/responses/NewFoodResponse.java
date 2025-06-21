package com.restaurant.management.responses;

import com.restaurant.management.DTO.FoodDTO;
import com.restaurant.management.DTO.ReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewFoodResponse {
    private List<FoodDTO> newestFoods;
    private FoodDTO topRatedFood;
    private List<ReviewDTO> featuredReviews;
}
