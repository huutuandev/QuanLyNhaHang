package com.restaurant.management.controller;

import com.restaurant.management.requests.CreatePaymentRequest;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.CreateMoMoResponse;
import com.restaurant.management.service.IBillService;
import com.restaurant.management.service.IMoMoService;
import com.restaurant.management.service.IVnPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for creating payments and handling payment webhook IPNs")
public class PaymentController {

    private final IMoMoService momoService;
    private final IVnPayService vnPayService;
    private final IBillService billService;

    @PostMapping("/momo")
    @Operation(summary = "Create MoMo Payment Link", description = "Generates a MoMo payment URL for deposit/bill payment.")
    public ResponseEntity<ApiResponse<String>> createMoMoPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        log.info("Request to create MoMo payment for amount: {}", dto.getAmount());
        CreateMoMoResponse response = momoService.createPayment(dto.getAmount(), dto.getOrderInfo());
        if (response != null && response.getPayUrl() != null) {
            log.info("MoMo payment URL created successfully");
            return ResponseEntity.ok(ApiResponse.success("MoMo payment created", response.getPayUrl()));
        } else {
            log.error("Failed to create MoMo payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Tạo thanh toán MoMo thất bại"));
        }
    }

    @PostMapping("/vnpay")
    @Operation(summary = "Create VNPAY Payment Link", description = "Generates a VNPAY payment URL for deposit/bill payment.")
    public ResponseEntity<ApiResponse<String>> createVnPayPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        log.info("Request to create VNPAY payment for amount: {}", dto.getAmount());
        String payUrl = vnPayService.createPayment(dto.getAmount(), dto.getOrderInfo());
        if (payUrl != null) {
            log.info("VNPAY payment URL created successfully");
            return ResponseEntity.ok(ApiResponse.success("VNPAY payment created", payUrl));
        } else {
            log.error("Failed to create VNPAY payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Tạo thanh toán VNPAY thất bại"));
        }
    }

    @PostMapping("/vnpay/ipn")
    @Operation(summary = "VNPAY Instant Payment Notification (IPN) Hook", description = "Called asynchronously by VNPAY to update payment transaction status.")
    public ResponseEntity<Map<String, String>> processVnPayIPN(@RequestParam Map<String, String> params) {
        log.info("Received VNPAY IPN notification: {}", params);
        Map<String, String> response = new HashMap<>();

        try {
            String secureHash = params.get("vnp_SecureHash");
            if (secureHash == null || !vnPayService.verifySignature(params, secureHash)) {
                log.warn("VNPAY IPN signature verification failed");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return ResponseEntity.ok(response);
            }

            String orderInfo = params.get("vnp_OrderInfo");
            int hashIndex = orderInfo != null ? orderInfo.indexOf('#') : -1;
            if (hashIndex == -1) {
                log.warn("VNPAY IPN failed: Reservation ID not found in orderInfo: {}", orderInfo);
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return ResponseEntity.ok(response);
            }

            Long reservationId = Long.parseLong(orderInfo.substring(hashIndex + 1).trim());
            String responseCode = params.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                double amount = Double.parseDouble(params.get("vnp_Amount")) / 100.0;
                billService.processPaymentIPN(reservationId, "VNPAY", amount);
                log.info("VNPAY IPN processed successfully for Reservation ID: {}", reservationId);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            } else {
                log.warn("VNPAY payment unsuccessful. Response code: {}", responseCode);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success (payment failed status registered)");
            }

        } catch (Exception e) {
            log.error("Error processing VNPAY IPN: ", e);
            response.put("RspCode", "99");
            response.put("Message", "Input required data invalid");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/momo/ipn")
    @Operation(summary = "MoMo Instant Payment Notification (IPN) Hook", description = "Called asynchronously by MoMo to update payment transaction status.")
    public ResponseEntity<Map<String, Object>> processMoMoIPN(@RequestBody Map<String, String> params) {
        log.info("Received MoMo IPN notification: {}", params);
        Map<String, Object> response = new HashMap<>();

        try {
            String signature = params.get("signature");
            if (signature == null || !momoService.verifySignature(params, signature)) {
                log.warn("MoMo IPN signature verification failed");
                response.put("resultCode", 97);
                response.put("message", "Invalid signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String orderInfo = params.get("orderInfo");
            int hashIndex = orderInfo != null ? orderInfo.indexOf('#') : -1;
            if (hashIndex == -1) {
                log.warn("MoMo IPN failed: Reservation ID not found in orderInfo: {}", orderInfo);
                response.put("resultCode", 1);
                response.put("message", "Order not found");
                return ResponseEntity.ok(response);
            }

            Long reservationId = Long.parseLong(orderInfo.substring(hashIndex + 1).trim());
            String resultCodeStr = params.get("resultCode");
            int resultCode = Integer.parseInt(resultCodeStr);

            if (resultCode == 0) {
                double amount = Double.parseDouble(params.get("amount"));
                billService.processPaymentIPN(reservationId, "MOMO", amount);
                log.info("MoMo IPN processed successfully for Reservation ID: {}", reservationId);
            } else {
                log.warn("MoMo payment unsuccessful. Result code: {}", resultCode);
            }

            response.put("resultCode", 0);
            response.put("message", "Success");

        } catch (Exception e) {
            log.error("Error processing MoMo IPN: ", e);
            response.put("resultCode", 99);
            response.put("message", "Internal Server Error");
        }

        return ResponseEntity.ok(response);
    }
}
