package com.restaurant.management.responses;

import com.restaurant.management.dto.FoodDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private Long id;
    private FoodDTO food;
    private LocalDateTime createdAt;
}
