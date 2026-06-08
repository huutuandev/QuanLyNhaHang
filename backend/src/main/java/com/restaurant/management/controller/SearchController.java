package com.restaurant.management.controller;

import com.restaurant.management.documents.FoodDocument;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IFoodSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Endpoints for Elasticsearch-powered food discovery and search suggestions")
public class SearchController {

    private final IFoodSearchService foodSearchService;

    @GetMapping("/foods")
    @Operation(summary = "Search foods", description = "Searches food items using full-text and fuzzy search on name, description, and category. Supports filtering and sorting.")
    public ResponseEntity<ApiResponse<PagedResponse<FoodDocument>>> searchFoods(
            @Parameter(description = "Search keyword for full-text and typo-tolerant search")
            @RequestParam(value = "q", required = false) String q,
            @Parameter(description = "Filter by exact category name")
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @Parameter(description = "Filter by minimum price")
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @Parameter(description = "Filter by maximum price")
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @Parameter(description = "Filter by availability status")
            @RequestParam(value = "available", required = false) Boolean available,
            @Parameter(description = "Sorting criteria: 'price_asc', 'price_desc', 'popular', 'bestseller', 'newest'")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "Zero-indexed page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        long startTime = System.currentTimeMillis();
        Page<FoodDocument> pageResult = foodSearchService.searchFoods(
                q, categoryName, minPrice, maxPrice, available, sort, PageRequest.of(page, size)
        );
        long duration = System.currentTimeMillis() - startTime;
        log.info("Search query: '{}', categoryName: {}, minPrice: {}, maxPrice: {}, available: {}, sort: {}. Results: {}. Execution time: {} ms.",
                q, categoryName, minPrice, maxPrice, available, sort, pageResult.getTotalElements(), duration);

        PagedResponse<FoodDocument> pagedResponse = new PagedResponse<>(pageResult, pageResult.getContent());
        return ResponseEntity.ok(ApiResponse.success("Search completed", pagedResponse));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get search suggestions", description = "Retrieves autocomplete suggestions (search-as-you-type) for a given prefix query.")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(
            @Parameter(description = "Prefix query keyword")
            @RequestParam("q") String q
    ) {
        log.info("Search suggestions query: '{}'", q);
        List<String> suggestions = foodSearchService.getSuggestions(q);
        return ResponseEntity.ok(ApiResponse.success("Suggestions retrieved", suggestions));
    }
}
