package com.restaurant.management.service.Impl;

import com.restaurant.management.dto.ReservationDTO;
import com.restaurant.management.dto.ReservationOrderDTO;
import com.restaurant.management.dto.UserDTO;


import com.restaurant.management.constant.BillStatusConstant;
import com.restaurant.management.constant.ReservationStatusConstant;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.ReservationEntity;
import com.restaurant.management.models.ReservationOrderEntity;
import com.restaurant.management.models.TableEntity;
import com.restaurant.management.respository.*;
import com.restaurant.management.service.IReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepo;
    private final UserRepository userRepo;
    private final FoodRepository foodRepo;
    private final BillRepository billRepo;
    private final ModelMapper modelMapper;
    private final TableRepository tableRepository;
    private static final int DEFAULT_DURATION_HOURS = 2;

    @Override
    public ReservationDTO createOrUpdate(Long userId, ReservationDTO dto) {
        ReservationEntity reservation = dto.getId() != null
                ? reservationRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + dto.getId()))
                : new ReservationEntity();
        mapDtoToEntity(dto, reservation);
        reservation.setCustomer(userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + userId)));
        TableEntity assignedTable = autoAssignTable(dto);
        reservation.setTable(assignedTable);
        setReservationOrders(dto, reservation);
        checkUserAlreadyBookedOnDate(userId, dto);
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
        if (reservation.getBill() != null) {
            reservation.getBill().setPaymentStatus(BillStatusConstant.CANCELLED);
        }
        reservationRepo.save(reservation);
    }

    @Override
    public Page<ReservationDTO> getAllByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationDate").descending());
        Page<ReservationEntity> reservationPage = reservationRepo.findByCustomerId(userId, pageable);
        return reservationPage.map(entity -> {
            ReservationDTO dto = modelMapper.map(entity, ReservationDTO.class);
            if (entity.getBill() != null) {
                dto.setPaymentStatus(entity.getBill().getPaymentStatus());
            }
            return dto;
        });
    }



    @Override
    public Page<ReservationDTO> getAllReservations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationDate").descending());
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

    private void checkUserAlreadyBookedOnDate(Long userId, ReservationDTO dto) {
        LocalDate reservationDate = dto.getReservationDate();
        List<ReservationEntity> existingReservations = reservationRepo
                .findAllByCustomerIdAndReservationDate(userId, reservationDate);
        for (ReservationEntity existing : existingReservations) {
            // Nếu là cập nhật chính bản ghi hiện tại thì bỏ qua
            if (dto.getId() != null && existing.getId().equals(dto.getId())) continue;
            // ❌ Nếu có bất kỳ bản ghi nào khác → lỗi
            throw new RuntimeException("Bạn đã đặt bàn vào ngày " + reservationDate + ". Không thể đặt thêm.");
        }
    }

    private TableEntity autoAssignTable(ReservationDTO dto) {
        LocalTime newStart = dto.getReservationTime();
        LocalTime newEnd = newStart.plusHours(DEFAULT_DURATION_HOURS);

        List<TableEntity> allActiveTables = tableRepository.findAll();

        TableEntity bestTable = null;
        int minSuitableCapacity = Integer.MAX_VALUE;

        for (TableEntity table : allActiveTables) {
            if (table.getCapacity() < dto.getNumberOfGuests()) {
                continue;
            }

            boolean isTableFree = isTableFree(
                    table,
                    dto.getReservationDate(),
                    newStart,
                    newEnd,
                    dto.getId()
            );

            if (isTableFree && table.getCapacity() < minSuitableCapacity) {
                bestTable = table;
                minSuitableCapacity = table.getCapacity();
            }
        }

        if (bestTable == null) {
            throw new RuntimeException("Không có bàn nào phù hợp cho "
                    + dto.getNumberOfGuests() + " khách vào khung giờ này.");
        }

        return bestTable;
    }

    // ===================== CORE CHECK: BÀN TRỐNG =====================
    private boolean isTableFree(TableEntity table, LocalDate date,
                                LocalTime newStart, LocalTime newEnd,
                                Long currentReservationId) {   // ← Thêm param này

        List<ReservationEntity> bookings = reservationRepo
                .findByTableIdAndReservationDate(table.getId(), date);

        return bookings.stream()
                .noneMatch(existing -> {
                    // ==================== QUAN TRỌNG ====================
                    // Bỏ qua chính reservation đang được update
                    if (currentReservationId != null &&
                            existing.getId().equals(currentReservationId)) {
                        return false;
                    }

                    LocalTime existingStart = existing.getReservationTime();
                    LocalTime existingEnd = existingStart.plusHours(DEFAULT_DURATION_HOURS);

                    // Overlap condition
                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long reservationId, String paymentStatus, String transId) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        BillEntity bill = billRepo.findByReservationId(reservationId);
        if (bill == null) {
            bill = BillEntity.builder()
                    .reservation(reservation)
                    .totalAmount(0.0)
                    .paidAmount(0.0)
                    .paymentMethod("MOMO")
                    .paymentStatus(BillStatusConstant.DEPOSIT_PAID)
                    .paidAt(LocalDateTime.now())
                    .build();
        } else {
            bill.setPaymentStatus(BillStatusConstant.DEPOSIT_PAID);
            bill.setPaidAt(LocalDateTime.now());
            bill.setPaymentMethod("MOMO");
        }
        billRepo.save(bill);

        reservation.setStatus(ReservationStatusConstant.CONFIRMED);
        reservationRepo.save(reservation);

        log.info("Reservation payment updated successfully. Reservation ID: {}, transId: {}", reservationId, transId);
    }
}

