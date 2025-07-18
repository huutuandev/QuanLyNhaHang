package com.restaurant.management.models;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TableNumber", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "Status", length = 50)
    private String status;

    @OneToMany(mappedBy = "table")
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "table")
    private List<ReservationEntity> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "table")
    private List<ChatSessionEntity> chatSessions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) status = "Available";
    }
}

