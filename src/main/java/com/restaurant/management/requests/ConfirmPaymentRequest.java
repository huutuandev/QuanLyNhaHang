package com.restaurant.management.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmPaymentRequest {
    private Long reservationId;
    private String paymentMethod;
}
