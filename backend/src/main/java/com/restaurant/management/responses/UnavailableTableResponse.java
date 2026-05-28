package com.restaurant.management.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnavailableTableResponse {
    private Long tableId;
    private LocalTime startTime;
}

