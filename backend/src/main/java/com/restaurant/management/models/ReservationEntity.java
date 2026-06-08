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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private UserEntity customer;

    @OneToOne(mappedBy = "reservation")
    private BillEntity bill;

    @ManyToOne
    @JoinColumn(name = "TableId")
    private TableEntity table;

    @Column(name = "ReservationDate")
    private LocalDate reservationDate;

    @Column(name = "ReservationistName", length = 50)
    private String reservationistName;

    @Column(name = "ReservationistPhone", nullable = true, unique = true, length = 50)
    private String reservationistPhone;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

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

    @OneToMany(mappedBy = "reservation",cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    private List<ReservationOrderEntity> reservationOrders = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
