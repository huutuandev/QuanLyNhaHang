package com.restaurant.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationOrderDTO {
    private Integer id;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private String note;
}
