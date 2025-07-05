package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.BillDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.BillEntity;
import com.restaurant.management.respository.BillRepository;
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
}
