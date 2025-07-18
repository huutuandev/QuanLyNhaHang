package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ReservationDTO;
import com.restaurant.management.DTO.ReservationOrderDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.ReservationEntity;
import com.restaurant.management.models.ReservationOrderEntity;
import com.restaurant.management.responses.UnavailableTableResponse;
import com.restaurant.management.respository.*;
import com.restaurant.management.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepo;
    private final UserRepository userRepo;
    private final TableRepository tableRepo;
    private final FoodRepository foodRepo;
    private final BillRepository billRepo;
    private final ModelMapper modelMapper;

    @Override
    public ReservationDTO createOrUpdate(UserDTO userDTO, ReservationDTO dto) {
        ReservationEntity reservation = dto.getId() != null
                ? reservationRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + dto.getId()))
                : new ReservationEntity();
        mapDtoToEntity(dto, reservation);
        reservation.setCustomer(userRepo.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + userDTO.getId())));
        reservation.setTable(tableRepo.findById(dto.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + dto.getTableId())));
        checkTimeSlotConflict(dto);
        setReservationOrders(dto, reservation);
        ReservationEntity saved = reservationRepo.save(reservation);
        return modelMapper.map(saved, ReservationDTO.class);
    }
    private void mapDtoToEntity(ReservationDTO dto, ReservationEntity reservation) {
        reservation.setReservationistName(dto.getReservationistName());
        reservation.setReservationistPhone(dto.getReservationistPhone());
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setReservationTime(dto.getReservationTime());
        reservation.setNumberOfGuests(dto.getNumberOfGuests());
        reservation.setNote(dto.getNote());
        reservation.setStatus(dto.getStatus());
        if (dto.getId() == null) {
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setReservationOrders(new ArrayList<>());
        }
    }
    private void setReservationOrders(ReservationDTO dto, ReservationEntity reservation) {
        reservation.getReservationOrders().clear();
        if (dto.getOrders() != null) {
            for (ReservationOrderDTO orderDTO : dto.getOrders()) {
                ReservationOrderEntity order = ReservationOrderEntity.builder()
                        .reservation(reservation)
                        .food(foodRepo.findById(orderDTO.getFoodId())
                                .orElseThrow(() -> new RuntimeException("Food not found with id: " + orderDTO.getFoodId())))
                        .quantity(orderDTO.getQuantity())
                        .note(orderDTO.getNote())
                        .build();
                reservation.getReservationOrders().add(order);
            }
        }
    }
    @Override
    public ReservationDTO getById(Long id) {
        ReservationEntity reservation = reservationRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: "+ id));
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    @Override
    public void delete(Long id) {
        ReservationEntity reservation = reservationRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found with id"+ id));
        reservation.setIsDeleted(true);
        reservationRepo.save(reservation);
    }
    @Override
    public List<ReservationDTO> getAllByUser(Long userId) {
        List<ReservationEntity> reservations = reservationRepo.findByCustomerId(userId);
        return reservations.stream()
                .map(entity -> modelMapper.map(entity,ReservationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UnavailableTableResponse> getUnavailableTablesWithTime(LocalDate date) {
        List<ReservationEntity> reservations = reservationRepo.findAllByReservationDate(date);

        return reservations.stream()
                .filter(r -> !r.getIsDeleted())
                .map(r -> {
                    LocalTime reservedTime = r.getReservationTime();
                    LocalTime endTime = calculateEndTime(reservedTime);
                    return new UnavailableTableResponse(
                            r.getTable().getId(),
                            reservedTime,
                            endTime
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getAllReservations() {
        List<ReservationEntity> list = reservationRepo.findAllByIsDeletedFalse();
        return list.stream().map(reservationEntity ->
                modelMapper.map(reservationEntity,ReservationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public ReservationDTO updateStatus(Long reservationId, String newStatus) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Lỗi không phân định"));
        reservation.setStatus(newStatus);
        if ("Completed".equalsIgnoreCase(newStatus)) {
            BillEntity bill = billRepo.findByReservationId(reservationId);
            if (bill != null && bill.getPaidAmount() < bill.getTotalAmount()) {
                bill.setPaidAmount(bill.getTotalAmount());
                billRepo.save(bill);
            }
        }
        reservationRepo.save(reservation);
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    private void checkTimeSlotConflict(ReservationDTO dto) {
        Long currentId = dto.getId() != null ? dto.getId() : -1L;
        List<ReservationEntity> existingReservations = reservationRepo.findAllByTableAndDate(
                dto.getTableId(),
                dto.getReservationDate(),
                currentId
        );
        LocalTime newTime = dto.getReservationTime();
        boolean isNewEvening = isEvening(newTime);

        for (ReservationEntity existing : existingReservations) {
            LocalTime existingTime = existing.getReservationTime();
            boolean isExistingEvening = isEvening(existingTime);

            if (isNewEvening && isExistingEvening) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bàn này đã có người đặt buổi tối.");
            }

            if (!isNewEvening && !isExistingEvening) {
                if (isTimeOverlap(newTime, existingTime, 4)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Bàn này đã có người đặt trong khoảng 4 tiếng gần đó.");
                }
            }
        }
    }

    private boolean isEvening(LocalTime time) {
        return !time.isBefore(LocalTime.of(18, 0));
    }
    private boolean isTimeOverlap(LocalTime t1, LocalTime t2, int durationHours) {
        LocalTime t1End = t1.plusHours(durationHours);
        LocalTime t2End = t2.plusHours(durationHours);
        return t1.isBefore(t2End) && t2.isBefore(t1End);
    }
    private LocalTime calculateEndTime(LocalTime startTime) {
        return isEvening(startTime) ? LocalTime.of(23, 59, 59) : startTime.plusHours(4);
    }

}
