package com.restaurant.management.service.Impl;

import com.restaurant.management.dto.BillDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.constant.BillStatusConstant;
import com.restaurant.management.constant.ReservationStatusConstant;
import com.restaurant.management.models.*;
import com.restaurant.management.requests.ConfirmPaymentRequest;
import com.restaurant.management.respository.BillRepository;
import com.restaurant.management.respository.OrderRepository;
import com.restaurant.management.respository.ReservationRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IBillService;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements IBillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ITableService tableService;
    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BillDTO> getAllBillByUser(UserDTO userDTO) {
        List<BillEntity> billEntities = billRepository.findByCashierId(userDTO.getId());
        return billEntities.stream()
                .map(billEntity -> modelMapper.map(billEntity, BillDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BillDTO getBillById(Long id) {
        BillEntity bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found by id"));
        return modelMapper.map(bill, BillDTO.class);
    }

    @Override
    public BillDTO createBill(BillDTO billDTO, UserDTO userDTO) {
        boolean hasOrder = billDTO.getOrderId() != null;
        boolean hasReservation = billDTO.getReservationId() != null;
        if (hasOrder == hasReservation) {
            throw new RuntimeException("Lỗi Giao Diện");
        }
        UserEntity user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        BillEntity.BillEntityBuilder builder = BillEntity.builder()
                .cashier(user)
                .totalAmount(billDTO.getTotalAmount())
                .paidAmount(billDTO.getPaidAmount())
                .paymentMethod(billDTO.getPaymentMethod());
        if (hasOrder) {
            OrderEntity order = orderRepository.findById(billDTO.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            if (billRepository.existsByOrder(order)) {
                throw new RuntimeException("Đơn hàng này đã được thanh toán");
            }
            builder.order(order);
        } else {
            ReservationEntity reservation = reservationRepository.findById(billDTO.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));
            if (billRepository.existsByReservation(reservation)) {
                throw new RuntimeException("Đặt bàn này đã được thanh toán");
            }
            builder.reservation(reservation);
        }
        BillEntity saved = billRepository.save(builder.build());
        return modelMapper.map(saved, BillDTO.class);
    }

    @Override
    @Transactional
    public BillDTO confirmPayment(ConfirmPaymentRequest request) {
        Long reservationId = request.getReservationId();
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId không được để trống");
        }
        BillEntity bill = getUnpaidBillByReservationId(reservationId);
        ReservationEntity reservation = getReservationById(reservationId);
        bill.setPaymentStatus(BillStatusConstant.DEPOSIT_PAID);
        bill.setPaidAt(LocalDateTime.now());
        assignTableToReservation(reservation);
        bill.setReservation(reservation);

        if (request.getPaymentMethod() != null) {
            bill.setPaymentMethod(request.getPaymentMethod());
        }

        BillEntity savedBill = billRepository.save(bill);
        return modelMapper.map(savedBill, BillDTO.class);
    }

    private BillEntity getUnpaidBillByReservationId(Long reservationId) {
        BillEntity bill = billRepository.findByReservationId(reservationId);
        if (bill == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn cho đặt bàn này");
        }
        if (BillStatusConstant.DEPOSIT_PAID.equals(bill.getPaymentStatus())) {
            throw new RuntimeException("Hóa đơn đã được cọc tiền");
        }
        return bill;
    }

    private ReservationEntity getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tồn tại đơn đặt bàn"));
    }

    private void assignTableToReservation(ReservationEntity reservation) {
        List<TableEntity> availableTables = tableService.getAvailableTables(
                reservation.getReservationDate(),
                reservation.getId()
        );

        if (availableTables.isEmpty()) {
            throw new RuntimeException("Không còn bàn trống trong ngày này.");
        }

        TableEntity chosenTable = availableTables.get(new Random().nextInt(availableTables.size()));
        reservation.setTable(chosenTable);
    }

    @Override
    @Transactional
    public void processPaymentIPN(Long reservationId, String paymentMethod, double amount) {
        log.info("Processing IPN payment for Reservation ID: {}, Method: {}, Amount: {}", reservationId, paymentMethod, amount);
        
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId));
                
        BillEntity bill = billRepository.findByReservationId(reservationId);
        if (bill == null) {
            throw new RuntimeException("Bill not found for reservation ID: " + reservationId);
        }
        
        // Prevent duplicate processing
        if (BillStatusConstant.DEPOSIT_PAID.equals(bill.getPaymentStatus())) {
            log.info("IPN payment already processed for Reservation ID: {}. Skipping.", reservationId);
            return;
        }
        
        // Validate amount (allow minor rounding differences)
        if (amount < bill.getPaidAmount() * 0.95) {
            log.warn("Payment amount mismatch! Expected: {}, Received: {}", bill.getPaidAmount(), amount);
            throw new IllegalArgumentException("Payment amount is incorrect");
        }
        
        bill.setPaymentStatus(BillStatusConstant.DEPOSIT_PAID);
        bill.setPaidAt(LocalDateTime.now());
        bill.setPaymentMethod(paymentMethod);
        
        // Confirm reservation table
        if (reservation.getTable() == null) {
            assignTableToReservation(reservation);
        }
        reservation.setStatus(ReservationStatusConstant.CONFIRMED);
        
        billRepository.save(bill);
        reservationRepository.save(reservation);
        
        log.info("IPN payment successfully processed and reservation confirmed for Reservation ID: {}", reservationId);
    }
}
