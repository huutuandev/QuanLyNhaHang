package com.restaurant.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDTO {
    private Long id;
    private Long orderId;
    private Long reservationId;
    private Double totalAmount;
    private Double paidAmount;
    private String paymentMethod;
    private String paymentStatus;
}
