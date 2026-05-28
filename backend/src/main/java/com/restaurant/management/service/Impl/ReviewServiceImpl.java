package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ReviewDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.constant.ReservationStatusConstant;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.FoodReviewEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.respository.ReservationOrderRepository;
import com.restaurant.management.respository.ReviewRepository;
import com.restaurant.management.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReservationOrderRepository reservationOrderRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReviewDTO createOrUpdate(UserDTO userDTO, ReviewDTO reviewDTO) {
        boolean hasOrdered = reservationOrderRepository
                .existsByReservation_Customer_IdAndFood_IdAndReservation_Status(
                        userDTO.getId(),
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
            if (userDTO.getId() == null || review.getUser().getId() != userDTO.getId()) {
                throw new IllegalArgumentException("Bạn không được phép chỉnh sửa đánh giá của người khác.");
            }
            review.setComment(reviewDTO.getComment());
            review.setRating(reviewDTO.getRating());
        } else {
            review = modelMapper.map(reviewDTO, FoodReviewEntity.class);
            review.setUser(modelMapper.map(userDTO, UserEntity.class));
        }

        FoodReviewEntity saved = reviewRepository.save(review);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    @Override
    public void deleteById(Long id, UserDTO userDTO) {
        FoodReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận có id = " + id));

        if (review.getUser().getId() != userDTO.getId()) {
            throw new RuntimeException("Bạn không thể xóa bình luận của người khác");
        }
        reviewRepository.deleteById(id);
    }

}
