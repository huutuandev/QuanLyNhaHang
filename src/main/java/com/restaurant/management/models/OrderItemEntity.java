package com.restaurant.management.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "OrderItems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "FoodId")
    private FoodEntity food;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Note")
    private String note;

    @Column(name = "Status")
    private String status;
}