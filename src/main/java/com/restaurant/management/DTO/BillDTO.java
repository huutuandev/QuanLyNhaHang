package com.restaurant.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDTO {
    private Long Id;
    private Long OrderId;
    private Long ReservationId;
    private Double TotalAmount;
    private Double paidAmount;
    private String PaymentMethod;
}
