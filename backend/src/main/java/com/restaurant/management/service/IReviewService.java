package com.restaurant.management.service;

import com.restaurant.management.dto.ReviewDTO;
import com.restaurant.management.dto.UserDTO;

public interface IReviewService {
    ReviewDTO createOrUpdate(UserDTO userDTO, ReviewDTO reviewDTO);
    void deleteById(Long id, UserDTO userDTO);
}
