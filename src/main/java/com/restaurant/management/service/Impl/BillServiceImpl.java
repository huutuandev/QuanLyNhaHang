package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.BillDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.OrderEntity;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.respository.BillRepository;
import com.restaurant.management.respository.OrderRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IBillService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements IBillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
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
        return modelMapper.map(bill,BillDTO.class);
    }

    @Override
    public BillDTO createBill(BillDTO billDTO, UserDTO userDTO) {
        OrderEntity order = orderRepository.findById(billDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy đơn hàng"));
        if (billRepository.existsByOrder(order)) {
            throw new RuntimeException("Đơn hàng này đã được thanh toán");
        }
        UserEntity user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy người dùng"));
        BillEntity billEntity = BillEntity.builder()
                .order(order)
                .cashier(user)
                .totalAmount(billDTO.getTotalAmount())
                .paymentMethod(billDTO.getPaymentMethod())
                .build();

        BillEntity saved = billRepository.save(billEntity);
        return modelMapper.map(saved, BillDTO.class);
    }

}
