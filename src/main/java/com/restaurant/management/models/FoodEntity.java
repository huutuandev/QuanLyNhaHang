package com.restaurant.management.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Column(name = "ImageUrl", length = 255)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private FoodCategoryEntity category;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "food")
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "food")
    private List<ReservationOrderEntity> reservationOrders = new ArrayList<>();

    @OneToMany(mappedBy = "food")
    private List<FoodReviewEntity> reviews = new ArrayList<>();

    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(FoodReviewEntity::getRating)
                .average()
                .orElse(0.0);
    }

}
