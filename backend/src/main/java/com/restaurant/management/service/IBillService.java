package com.restaurant.management.service;

import com.restaurant.management.DTO.BillDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.requests.ConfirmPaymentRequest;

import java.util.List;

public interface IBillService {
    List<BillDTO> getAllBillByUser(UserDTO userDTO);
    BillDTO getBillById(Long id);
    BillDTO createBill(BillDTO billDTO, UserDTO userDTO);
    BillDTO confirmPayment(ConfirmPaymentRequest confirmPaymentRequest);
}
