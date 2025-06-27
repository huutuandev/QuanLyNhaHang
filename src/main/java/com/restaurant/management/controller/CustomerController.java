package com.restaurant.management.controller;

import com.restaurant.management.DTO.*;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;
import com.restaurant.management.service.ICategoryService;
import com.restaurant.management.service.IFoodService;
import com.restaurant.management.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final IPostService postService;
    private final ICategoryService categoryService;
    private final IFoodService foodService;

    @GetMapping("/categories")
    public ResponseEntity<List<FoodCategoryDTO>> getAllCategoriesWithFoods()
    {
        List<FoodCategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<PostDTO> posts = postService.findAll(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Integer id){
        PostDTO postById = postService.findById(id);
        return ResponseEntity.ok(postById);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Integer id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size)
    {
        FoodCategoryDTO foodCategoryDTO = categoryService.findByIdWithFoods(id,page,size);
        return ResponseEntity.ok(foodCategoryDTO);
    }

    @GetMapping("/categories/foods/{id}")
    public ResponseEntity<FoodDetailResponse> getFoodsAndReviews(@PathVariable Integer id)
    {
        FoodDetailResponse foodDetailResponse = foodService.getFoodsAndReviews(id);
        return ResponseEntity.ok(foodDetailResponse);
    }

    @GetMapping("/home")
    public ResponseEntity<NewFoodResponse> getHomePageData() {
        NewFoodResponse newFoodResponse = foodService.getNewFoods();
        return ResponseEntity.ok(newFoodResponse);
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal UserEntity user) {
        UserDTO dto = new UserDTO(user.getFullName(), user.getPhoneNumber(), null, null,null,user.getEmail());
        return ResponseEntity.ok(dto);
    }
}
