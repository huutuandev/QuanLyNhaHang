package com.restaurant.management.service.Impl;

import com.restaurant.management.dto.ReviewDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.constant.ReservationStatusConstant;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.FoodReviewEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.respository.ReservationOrderRepository;
import com.restaurant.management.respository.ReviewRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReservationOrderRepository reservationOrderRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReviewDTO createOrUpdate(Long userId, ReviewDTO reviewDTO) {
        boolean hasOrdered = reservationOrderRepository
                .existsByReservation_Customer_IdAndFood_IdAndReservation_Status(
                        userId,
                        reviewDTO.getFoodId(),
                        ReservationStatusConstant.CONFIRMED
                );
        if (!hasOrdered) {
            throw new IllegalArgumentException("Bạn chưa từng đặt món này trong đơn đã xác nhận.");
        }
        FoodReviewEntity review;

        if (reviewDTO.getId() != null) {
            review = reviewRepository.findById(reviewDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá với ID: " + reviewDTO.getId()));
            if (userId == null || !review.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Bạn không được phép chỉnh sửa đánh giá của người khác.");
            }
            review.setComment(reviewDTO.getComment());
            review.setRating(reviewDTO.getRating());
        } else {
            review = modelMapper.map(reviewDTO, FoodReviewEntity.class);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            review.setUser(user);
        }

        FoodReviewEntity saved = reviewRepository.save(review);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    @Override
    public void deleteById(Long id, Long userId) {
        FoodReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận có id = " + id));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không thể xóa bình luận của người khác");
        }
        reviewRepository.deleteById(id);
    }

}
