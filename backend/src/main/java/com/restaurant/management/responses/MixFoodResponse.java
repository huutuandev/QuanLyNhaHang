package com.restaurant.management.responses;

import com.restaurant.management.dto.FoodDTO;
import com.restaurant.management.dto.ReviewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MixFoodResponse {
    private List<FoodDTO> newestFoods;
    private FoodDTO topRatedFood;
    private List<ReviewDTO> featuredReviews;
}
