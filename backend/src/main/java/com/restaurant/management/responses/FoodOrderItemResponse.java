package com.restaurant.management.responses;

import com.restaurant.management.dto.FoodDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrderItemResponse {
    private Long id;
    private FoodDTO food;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
}
