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
public class FoodDetailResponse {
    private FoodDTO foodDTO;
    private List<ReviewDTO> reviews;
    private List<FoodDTO> relatedFoods;
}
