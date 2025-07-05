package com.restaurant.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private Integer id;
    private Long userId;
    private Long foodId;
    private String username;
    private String comment;
    private Integer rating;
}
