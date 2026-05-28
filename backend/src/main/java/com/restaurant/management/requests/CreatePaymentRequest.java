package com.restaurant.management.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private long amount;
    private String orderInfo;
}
