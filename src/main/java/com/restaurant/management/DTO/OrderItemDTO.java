package com.restaurant.management.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long foodId;
    private Integer quantity;
    private String note;
    private String status;
}
