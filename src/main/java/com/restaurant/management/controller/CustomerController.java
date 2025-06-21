package com.restaurant.management.controller;

import com.restaurant.management.DTO.*;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;
import com.restaurant.management.service.ICategoryService;
import com.restaurant.management.service.IFoodService;
import com.restaurant.management.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<PostDTO>> getAllPosts()
    {
        List<PostDTO> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Integer id){
        PostDTO postById = postService.findById(id);
        return ResponseEntity.ok(postById);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<FoodCategoryDTO> getCategoryById(@PathVariable Integer id)
    {
        FoodCategoryDTO foodCategoryDTO = categoryService.findByIdWithFoods(id);
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

}
