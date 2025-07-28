package com.restaurant.management.service;

import com.restaurant.management.DTO.ReservationDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.responses.UnavailableTableResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservationService {
    ReservationDTO createOrUpdate(UserDTO userDTO, ReservationDTO dto);
    ReservationDTO getById(Long id);
    void cancel(Long id);
    Page<ReservationDTO> getAllByUser(Long userId, int page, int size);
    Page<ReservationDTO> getAllReservations(int page, int size);
    ReservationDTO updateStatus(Long reservationId, String newStatus);
}
