package com.restaurant.management.models;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "OrderId")
    private OrderEntity order;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ReservationId", unique =true)
    private ReservationEntity reservation;


    @ManyToOne
    @JoinColumn(name = "CashierId")
    private UserEntity cashier;

    @Column(name = "TotalAmount")
    private Double totalAmount;

    @Column(name = "PaidAmount")
    private Double paidAmount;

    @Column(name = "IsPaid", columnDefinition = "bit default 0")
    private Boolean isPaid = false;

    @Column(name = "PaidAt")
    private LocalDateTime paidAt;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @PrePersist
    public void prePersist() {
        if (paidAt == null) paidAt = LocalDateTime.now();
    }
}