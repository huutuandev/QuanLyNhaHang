package com.restaurant.management.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueRequest {
    private int month;
    private Double total;
}

