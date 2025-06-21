package com.restaurant.management.models;


import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "ReservationOrders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ReservationId")
    private ReservationEntity reservation;

    @ManyToOne
    @JoinColumn(name = "FoodId")
    private FoodEntity food;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Note")
    private String note;
}

