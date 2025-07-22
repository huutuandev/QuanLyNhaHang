package com.restaurant.management.controller;

import com.restaurant.management.constant.MoMoParameterConstant;
import com.restaurant.management.requests.CreatePaymentRequest;
import lombok.extern.slf4j.Slf4j;
import com.restaurant.management.responses.CreateMoMoResponse;
import com.restaurant.management.service.IMoMoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/momo")
@RequiredArgsConstructor
public class MoMoController {

    private final IMoMoService momoService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        CreateMoMoResponse response = momoService.createPayment(dto.getAmount(), dto.getOrderInfo());

        if (response != null && response.getPayUrl() != null) {
            return ResponseEntity.ok(response.getPayUrl());
        } else {
            return ResponseEntity.status(500).body("Tạo thanh toán thất bại");
        }
    }
    @PostMapping("/ipn-handler")
    public ResponseEntity<String> handleIpn(@RequestBody Map<String, Object> ipnData) {
        log.info("Nhận IPN từ MoMo: {}", ipnData);
        String orderId = (String) ipnData.get(MoMoParameterConstant.ORDER_ID);
        Integer resultCode = (Integer) ipnData.get(MoMoParameterConstant.RESULT_CODE);

        if (resultCode != null && resultCode == 0) {
            System.out.println("Thành Công");
            return ResponseEntity.ok("IPN received and handled successfully");

        } else {
            System.out.println("Thất bại");
            return ResponseEntity.ok("IPN received but payment failed");
        }
    }

}
