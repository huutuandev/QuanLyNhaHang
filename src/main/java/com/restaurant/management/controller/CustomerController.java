package com.restaurant.management.controller;

import com.restaurant.management.DTO.*;
import com.restaurant.management.responses.FoodDetailResponse;
import com.restaurant.management.responses.NewFoodResponse;
import com.restaurant.management.responses.UnavailableTableResponse;
import com.restaurant.management.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final IPostService postService;
    private final ICategoryService categoryService;
    private final IFoodService foodService;
    private final IReservationService reservationService;
    private final ITableService tableService;

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
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal UserDTO user) {
        return ResponseEntity.ok(user);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createOrUpdate(@AuthenticationPrincipal UserDTO user,@RequestBody ReservationDTO dto) {
        return ResponseEntity.ok(reservationService.createOrUpdate(user,dto));
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationDTO> get(@PathVariable Integer id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDTO>> getAll(@AuthenticationPrincipal UserDTO user) {
        return ResponseEntity.ok(reservationService.getAllByUser(user.getId()));
    }

    @GetMapping("/reservations/unavailable-tables")
    public ResponseEntity<List<UnavailableTableResponse>> getUnavailableTables(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<UnavailableTableResponse> result = reservationService.getUnavailableTablesWithTime(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tables")
    public ResponseEntity<List<TableDTO>> getAllTables(){
        List<TableDTO> tableDTOS = tableService.getAllTables();
        return ResponseEntity.ok(tableDTOS);
    }

}
