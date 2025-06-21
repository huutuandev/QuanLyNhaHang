package com.restaurant.management.models;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
@Entity
@Table(name = "Reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "TableId")
    private TableEntity table;

    @Column(name = "ReservationDate")
    private LocalDate reservationDate;

    @Column(name = "ReservationTime")
    private LocalTime reservationTime;

    @Column(name = "NumberOfGuests")
    private Integer numberOfGuests;

    @Column(name = "Note")
    private String note;

    @Column(name = "Status")
    private String status;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reservation")
    private List<ReservationOrderEntity> reservationOrders = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
