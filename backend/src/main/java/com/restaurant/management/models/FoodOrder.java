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
@Table(name = "FoodOrders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "OrderCode", unique = true, nullable = false)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private UserEntity user;

    @Column(name = "DeliveryAddress", nullable = false)
    private String deliveryAddress;

    @Column(name = "ReceiverName", nullable = false)
    private String receiverName;

    @Column(name = "ReceiverPhone", nullable = false)
    private String receiverPhone;

    @Column(name = "Note")
    private String note;

    @Column(name = "PaymentMethod", nullable = false)
    private String paymentMethod;

    @Column(name = "PaymentStatus", nullable = false)
    private String paymentStatus;

    @Column(name = "OrderStatus", nullable = false)
    private String orderStatus;

    @Column(name = "Subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "ShippingFee", nullable = false)
    private Double shippingFee;

    @Column(name = "TotalAmount", nullable = false)
    private Double totalAmount;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (orderCode == null) {
            orderCode = "ORD-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "foodOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FoodOrderItem> orderItems = new ArrayList<>();
}
