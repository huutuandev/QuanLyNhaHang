package com.restaurant.management.service;

import com.restaurant.management.dto.BillDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.requests.ConfirmPaymentRequest;

import java.util.List;

public interface IBillService {
    List<BillDTO> getAllBillByUser(Long userId);
    BillDTO getBillById(Long id);
    BillDTO createBill(BillDTO billDTO, Long userId);
    BillDTO confirmPayment(ConfirmPaymentRequest confirmPaymentRequest);
    void processPaymentIPN(Long reservationId, String paymentMethod, double amount);
}
