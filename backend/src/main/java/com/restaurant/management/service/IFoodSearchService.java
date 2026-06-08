package com.restaurant.management.service;

import com.restaurant.management.documents.FoodDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFoodSearchService {
    void indexFood(Long foodId);
    void deindexFood(Long foodId);
    void syncAllFoods();
    
    Page<FoodDocument> searchFoods(
            String query,
            String categoryName,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            String sort,
            Pageable pageable
    );

    List<String> getSuggestions(String query);
}
