package com.restaurant.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private String reservationistName;
    private String reservationistPhone;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer tableNumber;
    private Integer numberOfGuests;
    private String note;
    private String status;
    private List<ReservationOrderDTO> orders;
    private String paymentStatus;
}



