package com.restaurant.management.service;

import com.restaurant.management.DTO.ReservationDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.responses.UnavailableTableResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservationService {
    ReservationDTO createOrUpdate(UserDTO userDTO, ReservationDTO dto);
    ReservationDTO getById(Integer id);
    void delete(Long id);
    List<ReservationDTO> getAllByUser(Long userId);
    List<UnavailableTableResponse> getUnavailableTablesWithTime(LocalDate date);
}
