package com.restaurant.management.controller;

import com.restaurant.management.requests.CreatePaymentRequest;
import com.restaurant.management.responses.CreateMoMoResponse;
import com.restaurant.management.service.IMoMoService;
import com.restaurant.management.service.IVnPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IMoMoService momoService;
    private final IVnPayService vnPayService;

    @PostMapping("/momo")
    public ResponseEntity<?> createMoMoPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        CreateMoMoResponse response = momoService.createPayment(dto.getAmount(), dto.getOrderInfo());
        if (response != null && response.getPayUrl() != null) {
            return ResponseEntity.ok(response.getPayUrl());
        } else {
            return ResponseEntity.status(500).body("Tạo thanh toán MoMo thất bại");
        }
    }

    @PostMapping("/vnpay")
    public ResponseEntity<?> createVnPayPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        String payUrl = vnPayService.createPayment(dto.getAmount(), dto.getOrderInfo());
        if (payUrl != null) {
            return ResponseEntity.ok(payUrl);
        } else {
            return ResponseEntity.status(500).body("Tạo thanh toán VNPAY thất bại");
        }
    }

}
