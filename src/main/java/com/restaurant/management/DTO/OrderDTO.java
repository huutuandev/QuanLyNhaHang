package com.restaurant.management.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long tableId;
    private Long staffId;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> orderItems;
}
