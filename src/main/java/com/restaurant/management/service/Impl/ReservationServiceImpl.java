package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ReservationDTO;
import com.restaurant.management.DTO.ReservationOrderDTO;
import com.restaurant.management.DTO.UserDTO;

import java.util.Random;

import com.restaurant.management.constant.ReservationStatusConstant;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.ReservationEntity;
import com.restaurant.management.models.ReservationOrderEntity;
import com.restaurant.management.models.TableEntity;
import com.restaurant.management.responses.UnavailableTableResponse;
import com.restaurant.management.respository.*;
import com.restaurant.management.service.IReservationService;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        setReservationOrders(dto, reservation);
        checkUserAlreadyBookedOnDate(userDTO, dto);
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
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    @Override
    public void cancel(Long id) {
        ReservationEntity reservation = reservationRepo.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found with id" + id));
        reservation.setStatus(ReservationStatusConstant.CANCELLED);
        reservationRepo.save(reservation);
    }

    @Override
    public Page<ReservationDTO> getAllByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationDate").descending());
        Page<ReservationEntity> reservationPage = reservationRepo.findByCustomerId(userId, pageable);

        return reservationPage.map(entity -> {
            ReservationDTO dto = modelMapper.map(entity, ReservationDTO.class);
            boolean isPaid = billRepo.existsByReservation_IdAndIsPaidTrue(entity.getId());
            dto.setIsPaid(isPaid);
            return dto;
        });
    }



    @Override
    public Page<ReservationDTO> getAllReservations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationEntity> reservationpage = reservationRepo.findAllByIsDeletedFalse(pageable);
        return reservationpage.map(reservationEntity ->
                modelMapper.map(reservationEntity, ReservationDTO.class));
    }

    @Override
    public ReservationDTO updateStatus(Long reservationId, String newStatus) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Lỗi không phân định"));
        reservation.setStatus(newStatus);
        if (ReservationStatusConstant.COMPLETED.equalsIgnoreCase(newStatus)) {
            BillEntity bill = billRepo.findByReservationId(reservationId);
            if (bill != null && bill.getPaidAmount() < bill.getTotalAmount()) {
                bill.setPaidAmount(bill.getTotalAmount());
                billRepo.save(bill);
            }
        }
        reservationRepo.save(reservation);
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    private void checkUserAlreadyBookedOnDate(UserDTO userDTO, ReservationDTO dto) {
        LocalDate reservationDate = dto.getReservationDate();
        List<ReservationEntity> existingReservations = reservationRepo
                .findAllByCustomerIdAndReservationDate(userDTO.getId(), reservationDate);
        for (ReservationEntity existing : existingReservations) {
            // Nếu là cập nhật chính bản ghi hiện tại thì bỏ qua
            if (dto.getId() != null && existing.getId().equals(dto.getId())) continue;
            // ❌ Nếu có bất kỳ bản ghi nào khác → lỗi
            throw new RuntimeException("Bạn đã đặt bàn vào ngày " + reservationDate + ". Không thể đặt thêm.");
        }
    }


}
