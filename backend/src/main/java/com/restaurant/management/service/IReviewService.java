package com.restaurant.management.service;

import com.restaurant.management.dto.ReviewDTO;
import com.restaurant.management.dto.UserDTO;

public interface IReviewService {
    ReviewDTO createOrUpdate(Long userId, ReviewDTO reviewDTO);
    void deleteById(Long id, Long userId);
}
