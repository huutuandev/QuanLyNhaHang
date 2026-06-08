package com.restaurant.management.service.Impl;

import com.restaurant.management.documents.FoodDocument;
import com.restaurant.management.models.FoodEntity;
import com.restaurant.management.respository.FoodRepository;
import com.restaurant.management.service.IFoodSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodSearchServiceImpl implements IFoodSearchService {

    private final FoodRepository foodRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @PostConstruct
    public void init() {
        // Safely run sync on startup in a separate thread so it doesn't block boot
        new Thread(this::syncAllFoods).start();
    }

    @Override
    public void indexFood(Long foodId) {
        try {
            foodRepository.findById(foodId).ifPresent(food -> {
                FoodDocument doc = mapToDocument(food);
                elasticsearchRestTemplate.save(doc);
                log.info("Elasticsearch: Indexed food item ID: {}, name: {}", food.getId(), food.getName());
            });
        } catch (Exception e) {
            log.error("Elasticsearch: Failed to index food item with ID {}: {}", foodId, e.getMessage());
        }
    }

    @Override
    public void deindexFood(Long foodId) {
        try {
            elasticsearchRestTemplate.delete(String.valueOf(foodId), FoodDocument.class);
            log.info("Elasticsearch: Removed food item ID: {} from search index", foodId);
        } catch (Exception e) {
            log.error("Elasticsearch: Failed to deindex food item with ID {}: {}", foodId, e.getMessage());
        }
    }

    @Override
    public void syncAllFoods() {
        try {
            log.info("Elasticsearch: Syncing all foods to Elasticsearch...");
            List<FoodEntity> all = foodRepository.findAll();
            List<FoodDocument> docs = all.stream().map(this::mapToDocument).collect(Collectors.toList());
            if (!docs.isEmpty()) {
                elasticsearchRestTemplate.save(docs);
            }
            log.info("Elasticsearch: Successfully synced {} foods.", docs.size());
        } catch (Exception e) {
            log.warn("Elasticsearch: Sync failed (ES is probably offline): {}", e.getMessage());
        }
    }

    @Override
    public Page<FoodDocument> searchFoods(
            String query,
            String categoryName,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            String sort,
            Pageable pageable
    ) {
        long startTime = System.currentTimeMillis();
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (query != null && !query.trim().isEmpty()) {
                MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(query, "name", "description", "categoryName")
                        .fuzziness("AUTO")
                        .prefixLength(2)
                        .maxExpansions(50);
                boolQuery.must(matchQuery);
            }

            if (categoryName != null && !categoryName.trim().isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("categoryName", categoryName));
            }
            if (minPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice));
            }
            if (maxPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").lte(maxPrice));
            }
            if (available != null) {
                boolQuery.filter(QueryBuilders.termQuery("available", available));
            }

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(pageable);

            // Sorting
            if ("price_asc".equalsIgnoreCase(sort)) {
                queryBuilder.withSorts(SortBuilders.fieldSort("price").order(SortOrder.ASC));
            } else if ("price_desc".equalsIgnoreCase(sort)) {
                queryBuilder.withSorts(SortBuilders.fieldSort("price").order(SortOrder.DESC));
            } else if ("popular".equalsIgnoreCase(sort) || "bestseller".equalsIgnoreCase(sort)) {
                queryBuilder.withSorts(SortBuilders.fieldSort("popularityScore").order(SortOrder.DESC));
            } else if ("newest".equalsIgnoreCase(sort)) {
                queryBuilder.withSorts(SortBuilders.fieldSort("createdAt").order(SortOrder.DESC));
            } else {
                queryBuilder.withSorts(SortBuilders.scoreSort().order(SortOrder.DESC));
            }

            SearchHits<FoodDocument> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), FoodDocument.class);
            List<FoodDocument> content = searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            long totalHits = searchHits.getTotalHits();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Search query: '{}', categoryName: {}, minPrice: {}, maxPrice: {}, available: {}, sort: {}. Results count: {}. Execution time: {} ms.",
                    query, categoryName, minPrice, maxPrice, available, sort, totalHits, duration);
            
            return new PageImpl<>(content, pageable, totalHits);

        } catch (Exception e) {
            log.error("Elasticsearch: Search failed, falling back to database. Error details: {}", e.getMessage());
            return fallbackSearch(query, categoryName, minPrice, maxPrice, available, sort, pageable, startTime);
        }
    }

    @Override
    public List<String> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchPhrasePrefixQuery("name", query))
                    .withPageable(PageRequest.of(0, 10))
                    .build();
            SearchHits<FoodDocument> hits = elasticsearchRestTemplate.search(searchQuery, FoodDocument.class);
            return hits.stream()
                    .map(hit -> hit.getContent().getName())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Elasticsearch: Suggestions query failed, falling back to database: {}", e.getMessage());
            // Fallback suggestions: query DB
            List<FoodEntity> foods = foodRepository.findAll();
            String qLower = query.toLowerCase();
            return foods.stream()
                    .map(FoodEntity::getName)
                    .filter(name -> name != null && name.toLowerCase().contains(qLower))
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());
        }
    }

    private Page<FoodDocument> fallbackSearch(
            String query,
            String categoryName,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            String sort,
            Pageable pageable,
            long startTime
    ) {
        List<FoodEntity> allFoods = foodRepository.findAll();
        
        // Map and filter
        List<FoodDocument> documents = allFoods.stream()
                .map(this::mapToDocument)
                .filter(doc -> {
                    if (query != null && !query.trim().isEmpty()) {
                        String qLower = query.toLowerCase();
                        boolean matchName = doc.getName() != null && doc.getName().toLowerCase().contains(qLower);
                        boolean matchDesc = doc.getDescription() != null && doc.getDescription().toLowerCase().contains(qLower);
                        boolean matchCat = doc.getCategoryName() != null && doc.getCategoryName().toLowerCase().contains(qLower);
                        if (!matchName && !matchDesc && !matchCat) return false;
                    }
                    if (categoryName != null && !categoryName.trim().isEmpty()) {
                        if (doc.getCategoryName() == null || !doc.getCategoryName().equalsIgnoreCase(categoryName)) return false;
                    }
                    if (minPrice != null && doc.getPrice() < minPrice) return false;
                    if (maxPrice != null && doc.getPrice() > maxPrice) return false;
                    if (available != null && !doc.getAvailable().equals(available)) return false;
                    return true;
                })
                .collect(Collectors.toList());

        // Sort
        if ("price_asc".equalsIgnoreCase(sort)) {
            documents.sort(java.util.Comparator.comparing(FoodDocument::getPrice));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            documents.sort(java.util.Comparator.comparing(FoodDocument::getPrice).reversed());
        } else if ("popular".equalsIgnoreCase(sort) || "bestseller".equalsIgnoreCase(sort)) {
            documents.sort(java.util.Comparator.comparing(FoodDocument::getPopularityScore).reversed());
        } else if ("newest".equalsIgnoreCase(sort)) {
            documents.sort(java.util.Comparator.comparing(FoodDocument::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed());
        }

        // Paginate
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), documents.size());
        List<FoodDocument> pageContent = new ArrayList<>();
        if (start < documents.size()) {
            pageContent = documents.subList(start, end);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("JPA Fallback Search query: '{}', categoryName: {}, minPrice: {}, maxPrice: {}, available: {}, sort: {}. Results count: {}. Execution time: {} ms.",
                query, categoryName, minPrice, maxPrice, available, sort, documents.size(), duration);

        return new PageImpl<>(pageContent, pageable, documents.size());
    }

    private FoodDocument mapToDocument(FoodEntity food) {
        if (food == null) return null;
        
        String categoryName = food.getCategory() != null ? food.getCategory().getName() : "Uncategorized";
        int popularityScore = (food.getOrderItems() != null ? food.getOrderItems().size() : 0)
                + (food.getReservationOrders() != null ? food.getReservationOrders().size() : 0)
                + (food.getReviews() != null ? food.getReviews().size() : 0);

        Date createdAtDate = null;
        if (food.getCreatedAt() != null) {
            createdAtDate = java.sql.Timestamp.valueOf(food.getCreatedAt());
        }

        return FoodDocument.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .categoryName(categoryName)
                .price(food.getPrice())
                .image(food.getImageUrl())
                .available(food.getIsDeleted() == null ? true : !food.getIsDeleted())
                .popularityScore(popularityScore)
                .createdAt(createdAtDate)
                .build();
    }
}
