package com.restaurant.management.service;

import com.restaurant.management.DTO.ReviewDTO;
import com.restaurant.management.DTO.UserDTO;

public interface IReviewService {
    ReviewDTO createOrUpdate(UserDTO userDTO, ReviewDTO reviewDTO);
    void deleteById(Long id, UserDTO userDTO);
}
