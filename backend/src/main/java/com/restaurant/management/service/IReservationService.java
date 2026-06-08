package com.restaurant.management.service;

import com.restaurant.management.dto.ReservationDTO;
import com.restaurant.management.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface IReservationService {
    ReservationDTO createOrUpdate(UserDTO userDTO, ReservationDTO dto);
    ReservationDTO getById(Long id);
    void cancel(Long id);
    Page<ReservationDTO> getAllByUser(Long userId, int page, int size);
    Page<ReservationDTO> getAllReservations(int page, int size);
    ReservationDTO updateStatus(Long reservationId, String newStatus);
}
