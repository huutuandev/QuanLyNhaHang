package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.*;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.FoodCategoryEntity;
import com.restaurant.management.models.FoodEntity;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;
import com.restaurant.management.respository.FoodRepository;
import com.restaurant.management.respository.ReviewRepository;
import com.restaurant.management.service.IFoodService;
import com.restaurant.management.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final FoodRepository foodRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Override
    public FoodDetailResponse getFoodsAndReviews(Long id) {
        FoodEntity food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        List<ReviewDTO> reviewDTOs = food.getReviews().stream()
                .map(r -> {
                    return ReviewDTO.builder()
                            .id(r.getId())
                            .userId(r.getUser().getId())
                            .username(r.getUser().getFullName())
                            .comment(r.getComment())
                            .rating(r.getRating())
                            .build();
                })
                .collect(Collectors.toList());
        FoodDTO foodDTO = FoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .build();
        List<FoodDTO> relatedFoods = food.getCategory().getFoods().stream()
                .filter(f -> !f.getId().equals(id))
                .limit(3)
                .map(f -> FoodDTO.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .description(f.getDescription())
                        .price(f.getPrice())
                        .imageUrl(f.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return FoodDetailResponse.builder()
                .foodDTO(foodDTO)
                .reviews(reviewDTOs)
                .relatedFoods(relatedFoods)
                .build();
    }

    @Override
    public NewFoodResponse getNewFoods() {
        List<FoodDTO> newestFoods = foodRepository.findAllByIsDeletedFalse(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .limit(4)
                .map(food -> FoodDTO.builder()
                        .id(food.getId())
                        .name(food.getName())
                        .description(food.getDescription())
                        .price(food.getPrice())
                        .imageUrl(food.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        FoodEntity topRated = foodRepository.findAllByIsDeletedFalse()
                .stream()
                .max(Comparator.comparingDouble(FoodEntity::getAverageRating))
                .orElse(null);

        FoodDTO topRatedDTO = topRated != null ? FoodDTO.builder()
                .id(topRated.getId())
                .name(topRated.getName())
                .description(topRated.getDescription())
                .price(topRated.getPrice())
                .imageUrl(topRated.getImageUrl())
                .build() : null;

        List<ReviewDTO> featuredReviews = reviewRepository.findAllByOrderByRatingDesc()
                .stream()
                .limit(4)
                .map(review -> ReviewDTO.builder()
                        .id(review.getId())
                        .username(review.getUser().getFullName())
                        .userId(review.getUser().getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .build())
                .collect(Collectors.toList());
        return NewFoodResponse.builder()
                .newestFoods(newestFoods)
                .topRatedFood(topRatedDTO)
                .featuredReviews(featuredReviews)
                .build();
    }

    @Override
    public List<FoodDTO> getAllFoods() {
        List<FoodEntity> foodEntities = foodRepository.findAllByIsDeletedFalse();
        return foodEntities.stream().map(foodEntity -> modelMapper.map(foodEntity, FoodDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public FoodDTO getById(Long id) {
        FoodEntity food = foodRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy"));
        return modelMapper.map(food,FoodDTO.class);
    }

    @Override
    public FoodDTO createOrUpdate(FoodDTO foodDTO) {
        FoodEntity food;
        if (foodDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Category không được null");
        }
        if(foodDTO.getId() != null){
            food = foodRepository.findById(foodDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy"));
            modelMapper.map(foodDTO,food);
        }
        else {
            food = modelMapper.map(foodDTO, FoodEntity.class);
        }
        FoodCategoryEntity foodCategoryEntity = new FoodCategoryEntity();
        foodCategoryEntity.setId(foodDTO.getCategoryId());
        food.setCategory(foodCategoryEntity);
        FoodEntity saved = foodRepository.save(food);
        return modelMapper.map(saved,FoodDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        FoodEntity foodEntity = foodRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy"));
        foodEntity.setIsDeleted(true);
        foodRepository.save(foodEntity);
    }
}
