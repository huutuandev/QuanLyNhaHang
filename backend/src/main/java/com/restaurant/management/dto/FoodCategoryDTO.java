package com.restaurant.management.dto;

import com.restaurant.management.responses.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodCategoryDTO {
    private Long id;
    private String name;
    private PagedResponse<FoodDTO> foods;
}
