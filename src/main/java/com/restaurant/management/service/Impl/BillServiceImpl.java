package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.BillDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.OrderEntity;
import com.restaurant.management.models.ReservationEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.requests.ConfirmPaymentRequest;
import com.restaurant.management.respository.BillRepository;
import com.restaurant.management.respository.OrderRepository;
import com.restaurant.management.respository.ReservationRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IBillService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements IBillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
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
                .isPaid(false)
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
    public BillDTO confirmPayment(ConfirmPaymentRequest confirmPaymentRequest) {
        if (confirmPaymentRequest.getReservationId() == null) {
            throw new IllegalArgumentException("reservationId không được để trống");
        }
        BillEntity bill = billRepository.findByReservationId(confirmPaymentRequest.getReservationId());
        if (bill == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn cho đặt bàn này");
        }
        if (Boolean.TRUE.equals(bill.getIsPaid())) {
            throw new RuntimeException("Hóa đơn đã được thanh toán");
        }
        bill.setIsPaid(true);
        bill.setPaidAt(LocalDateTime.now());

        if (confirmPaymentRequest.getPaymentMethod() != null) {
            bill.setPaymentMethod(confirmPaymentRequest.getPaymentMethod());
        }
        BillEntity saved = billRepository.save(bill);
        return modelMapper.map(saved, BillDTO.class);
    }
}
