package com.restaurant.management.DTO;

import com.restaurant.management.responses.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodCategoryDTO {
    private Long id;
    private String name;
    private PagedResponse<FoodDTO> foods;
}
